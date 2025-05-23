package com.example.repair.service.impl;

import com.example.repair.entity.Material;
import com.example.repair.entity.User;
import com.example.repair.entity.WorkHour;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.Role;
import com.example.repair.enums.Specialty;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.repository.MaterialRepository;
import com.example.repair.repository.UserRepository;
import com.example.repair.repository.WorkHourRepository;
import com.example.repair.repository.WorkOrderRepository;
import com.example.repair.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final WorkHourRepository workHourRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVehicleRepairStatistics(Instant startDate, Instant endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询时间范围内的所有已完成工单
        List<WorkOrder> completedOrders = workOrderRepository.findByStatusAndCompletedAtBetween(
                WorkOrderStatus.COMPLETED, startDate, endDate);

        // 按车型分组统计维修次数
        Map<String, Long> repairsByModel = completedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getVehicle().getModel(),
                        Collectors.counting()
                ));

        // 按车型分组统计平均维修成本
        Map<String, BigDecimal> avgCostByModel = completedOrders.stream()
                .filter(order -> order.getTotalCost() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getVehicle().getModel(),
                        Collectors.averagingDouble(order -> order.getTotalCost().doubleValue())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BigDecimal.valueOf(entry.getValue())
                ));

        // 按车型分组统计平均维修时间（小时）
        Map<String, Double> avgTimeByModel = completedOrders.stream()
                .filter(order -> order.getCompletedAt() != null && order.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        order -> order.getVehicle().getModel(),
                        Collectors.averagingDouble(order ->
                                ChronoUnit.HOURS.between(order.getCreatedAt(), order.getCompletedAt())
                        )
                ));

        statistics.put("repairsByModel", repairsByModel);
        statistics.put("avgCostByModel", avgCostByModel);
        statistics.put("avgTimeByModel", avgTimeByModel);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCostAnalysis(Instant startDate, Instant endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询时间范围内的所有已完成工单
        List<WorkOrder> completedOrders = workOrderRepository.findByStatusAndCompletedAtBetween(
                WorkOrderStatus.COMPLETED, startDate, endDate);

        // 获取所有工单ID
        List<Long> orderIds = completedOrders.stream()
                .map(WorkOrder::getId)
                .collect(Collectors.toList());

        // 查询这些工单的材料成本
        List<Material> materials = materialRepository.findByWorkOrderIdIn(orderIds);

        // 查询这些工单的工时成本
        List<WorkHour> workHours = workHourRepository.findByWorkOrderIdIn(orderIds);

        // 总收入
        BigDecimal totalRevenue = completedOrders.stream()
                .map(WorkOrder::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 材料总成本
        BigDecimal materialCost = materials.stream()
                .map(material -> material.getUnitPrice().multiply(BigDecimal.valueOf(material.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 工时总成本
        BigDecimal laborCost = workHours.stream()
                .map(hour -> hour.getHourlyRate().multiply(hour.getHours()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总利润
        BigDecimal totalProfit = totalRevenue.subtract(materialCost).subtract(laborCost);

        // 利润率
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // 按月统计成本和收入
        Map<String, BigDecimal> revenueByMonth = new HashMap<>();
        Map<String, BigDecimal> costByMonth = new HashMap<>();
        Map<String, BigDecimal> profitByMonth = new HashMap<>();

        for (WorkOrder order : completedOrders) {
            if (order.getCompletedAt() != null && order.getTotalCost() != null) {
                String month = order.getCompletedAt().toString().substring(0, 7); // YYYY-MM

                // 累加收入
                revenueByMonth.put(month, revenueByMonth.getOrDefault(month, BigDecimal.ZERO)
                        .add(order.getTotalCost()));

                // 计算该订单的材料成本
                BigDecimal orderMaterialCost = materials.stream()
                        .filter(m -> Objects.equals(m.getWorkOrder().getId(), order.getId()))
                        .map(m -> m.getUnitPrice().multiply(BigDecimal.valueOf(m.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 计算该订单的工时成本
                BigDecimal orderLaborCost = workHours.stream()
                        .filter(h -> Objects.equals(h.getWorkOrder().getId(), order.getId()))
                        .map(h -> h.getHourlyRate().multiply(h.getHours()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal orderTotalCost = orderMaterialCost.add(orderLaborCost);

                // 累加成本
                costByMonth.put(month, costByMonth.getOrDefault(month, BigDecimal.ZERO)
                        .add(orderTotalCost));

                // 计算利润
                BigDecimal orderProfit = order.getTotalCost().subtract(orderTotalCost);
                profitByMonth.put(month, profitByMonth.getOrDefault(month, BigDecimal.ZERO)
                        .add(orderProfit));
            }
        }

        statistics.put("totalRevenue", totalRevenue);
        statistics.put("materialCost", materialCost);
        statistics.put("laborCost", laborCost);
        statistics.put("totalProfit", totalProfit);
        statistics.put("profitMargin", profitMargin);
        statistics.put("revenueByMonth", revenueByMonth);
        statistics.put("costByMonth", costByMonth);
        statistics.put("profitByMonth", profitByMonth);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNegativeFeedbackStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 查询所有包含反馈的工单
        List<WorkOrder> ordersWithFeedback = workOrderRepository.findByFeedbackIsNotNull();

        // 筛选出负面反馈（这里简单通过关键词筛选，实际应用中可能需要更复杂的情感分析）
        List<String> negativeKeywords = Arrays.asList("不满意", "差", "失望", "问题", "投诉", "退款", "糟糕", "慢", "贵");

        List<WorkOrder> ordersWithNegativeFeedback = ordersWithFeedback.stream()
                .filter(order -> {
                    String feedback = order.getFeedback().toLowerCase();
                    return negativeKeywords.stream().anyMatch(feedback::contains);
                })
                .collect(Collectors.toList());

        // 负面反馈率
        double negativeFeedbackRate = ordersWithFeedback.isEmpty() ? 0 :
                (double) ordersWithNegativeFeedback.size() / ordersWithFeedback.size() * 100;

        // 按维修工统计负面反馈
        Map<String, Long> negativeFeedbackByMechanic = new HashMap<>();
        for (WorkOrder order : ordersWithNegativeFeedback) {
            for (User mechanic : order.getMechanics()) {
                String mechanicName = mechanic.getFullName() != null ? mechanic.getFullName() : mechanic.getUsername();
                negativeFeedbackByMechanic.put(
                        mechanicName,
                        negativeFeedbackByMechanic.getOrDefault(mechanicName, 0L) + 1
                );
            }
        }

        // 按车型统计负面反馈
        Map<String, Long> negativeFeedbackByModel = ordersWithNegativeFeedback.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getVehicle().getModel(),
                        Collectors.counting()
                ));

        // 负面反馈内容分析，提取最常见的问题
        Map<String, Long> commonIssues = new HashMap<>();
        for (String keyword : negativeKeywords) {
            long count = ordersWithNegativeFeedback.stream()
                    .filter(order -> order.getFeedback().toLowerCase().contains(keyword))
                    .count();
            if (count > 0) {
                commonIssues.put(keyword, count);
            }
        }

        statistics.put("totalFeedbackCount", ordersWithFeedback.size());
        statistics.put("negativeFeedbackCount", ordersWithNegativeFeedback.size());
        statistics.put("negativeFeedbackRate", negativeFeedbackRate);
        statistics.put("negativeFeedbackByMechanic", negativeFeedbackByMechanic);
        statistics.put("negativeFeedbackByModel", negativeFeedbackByModel);
        statistics.put("commonIssues", commonIssues);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUnfinishedTasksStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 查询所有未完成的工单
        List<WorkOrder> pendingOrders = workOrderRepository.findByStatus(WorkOrderStatus.PENDING);
        List<WorkOrder> inProgressOrders = workOrderRepository.findByStatus(WorkOrderStatus.IN_PROGRESS);

        // 合并所有未完成工单
        List<WorkOrder> unfinishedOrders = new ArrayList<>();
        unfinishedOrders.addAll(pendingOrders);
        unfinishedOrders.addAll(inProgressOrders);

        // 按等待时间分组（超过7天、3-7天、1-3天、24小时内）
        Map<String, List<WorkOrder>> ordersByWaitTime = new HashMap<>();
        Instant now = Instant.now();

        List<WorkOrder> waitingOver7Days = unfinishedOrders.stream()
                .filter(order -> ChronoUnit.DAYS.between(order.getCreatedAt(), now) > 7)
                .collect(Collectors.toList());

        List<WorkOrder> waiting3To7Days = unfinishedOrders.stream()
                .filter(order -> {
                    long days = ChronoUnit.DAYS.between(order.getCreatedAt(), now);
                    return days >= 3 && days <= 7;
                })
                .collect(Collectors.toList());

        List<WorkOrder> waiting1To3Days = unfinishedOrders.stream()
                .filter(order -> {
                    long days = ChronoUnit.DAYS.between(order.getCreatedAt(), now);
                    return days >= 1 && days < 3;
                })
                .collect(Collectors.toList());

        List<WorkOrder> waitingLessThan24Hours = unfinishedOrders.stream()
                .filter(order -> ChronoUnit.DAYS.between(order.getCreatedAt(), now) < 1)
                .collect(Collectors.toList());

        ordersByWaitTime.put("over7Days", waitingOver7Days);
        ordersByWaitTime.put("3to7Days", waiting3To7Days);
        ordersByWaitTime.put("1to3Days", waiting1To3Days);
        ordersByWaitTime.put("under24Hours", waitingLessThan24Hours);

        // 计算平均等待时间
        double avgWaitTimeHours = unfinishedOrders.isEmpty() ? 0 :
                unfinishedOrders.stream()
                        .mapToLong(order -> ChronoUnit.HOURS.between(order.getCreatedAt(), now))
                        .average()
                        .orElse(0);

        // 按车型统计未完成工单
        Map<String, Long> unfinishedByModel = unfinishedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getVehicle().getModel(),
                        Collectors.counting()
                ));

        // 按分配状态统计
        long unassignedCount = unfinishedOrders.stream()
                .filter(order -> order.getMechanics().isEmpty())
                .count();

        long assignedCount = unfinishedOrders.size() - unassignedCount;

        statistics.put("totalUnfinishedCount", unfinishedOrders.size());
        statistics.put("pendingCount", pendingOrders.size());
        statistics.put("inProgressCount", inProgressOrders.size());
        statistics.put("waitingOver7Days", waitingOver7Days.size());
        statistics.put("waiting3To7Days", waiting3To7Days.size());
        statistics.put("waiting1To3Days", waiting1To3Days.size());
        statistics.put("waitingLessThan24Hours", waitingLessThan24Hours.size());
        statistics.put("avgWaitTimeHours", avgWaitTimeHours);
        statistics.put("unfinishedByModel", unfinishedByModel);
        statistics.put("unassignedCount", unassignedCount);
        statistics.put("assignedCount", assignedCount);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWorkerTasksStatistics(Instant startDate, Instant endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询所有技师
        List<User> mechanics = userRepository.findAllByRole(Role.MECHANIC);

        // 查询时间范围内的工单
        List<WorkOrder> orders = workOrderRepository.findByCreatedAtBetween(startDate, endDate);

        // 按专业统计完成工单数量
        Map<Specialty, Long> tasksBySpecialty = new HashMap<>();
        for (Specialty specialty : Specialty.values()) {
            long count = 0;
            for (WorkOrder order : orders) {
                if (order.getStatus() == WorkOrderStatus.COMPLETED) {
                    for (User mechanic : order.getMechanics()) {
                        if (mechanic.getSpecialty() == specialty) {
                            count++;
                            break; // 一个订单只计算一次
                        }
                    }
                }
            }
            tasksBySpecialty.put(specialty, count);
        }

        // 按技师统计任务完成情况
        List<Map<String, Object>> mechanicPerformance = new ArrayList<>();
        for (User mechanic : mechanics) {
            Map<String, Object> performance = new HashMap<>();
            performance.put("mechanicId", mechanic.getId());
            performance.put("mechanicName", mechanic.getFullName() != null ? mechanic.getFullName() : mechanic.getUsername());
            performance.put("specialty", mechanic.getSpecialty());

            // 统计该技师参与的订单
            List<WorkOrder> mechanicOrders = orders.stream()
                    .filter(order -> order.getMechanics().contains(mechanic))
                    .collect(Collectors.toList());

            // 完成的订单
            long completedCount = mechanicOrders.stream()
                    .filter(order -> order.getStatus() == WorkOrderStatus.COMPLETED)
                    .count();

            // 进行中的订单
            long inProgressCount = mechanicOrders.stream()
                    .filter(order -> order.getStatus() == WorkOrderStatus.IN_PROGRESS)
                    .count();

            // 拒绝的订单（这需要额外数据来追踪，这里简化处理）
            long rejectedCount = 0;

            // 计算平均完成时间
            double avgCompletionHours = mechanicOrders.stream()
                    .filter(order -> order.getStatus() == WorkOrderStatus.COMPLETED && order.getCompletedAt() != null)
                    .mapToLong(order -> ChronoUnit.HOURS.between(order.getCreatedAt(), order.getCompletedAt()))
                    .average()
                    .orElse(0);

            performance.put("totalOrders", mechanicOrders.size());
            performance.put("completedOrders", completedCount);
            performance.put("inProgressOrders", inProgressCount);
            performance.put("rejectedOrders", rejectedCount);
            performance.put("avgCompletionHours", avgCompletionHours);

            mechanicPerformance.add(performance);
        }

        statistics.put("totalMechanics", mechanics.size());
        statistics.put("tasksBySpecialty", tasksBySpecialty);
        statistics.put("mechanicPerformance", mechanicPerformance);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWorkerIncomeStatistics(Instant startDate, Instant endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询所有技师
        List<User> mechanics = userRepository.findAllByRole(Role.MECHANIC);

        // 查询该时间段内的工时记录
        List<WorkHour> workHours = workHourRepository.findByWorkOrderCompletedAtBetween(startDate, endDate);

        // 按技师统计总收入
        Map<Long, BigDecimal> incomeByMechanicId = new HashMap<>();
        Map<Long, BigDecimal> hoursByMechanicId = new HashMap<>();

        for (WorkHour workHour : workHours) {
            Long mechanicId = workHour.getMechanic().getId();
            BigDecimal income = workHour.getHours().multiply(workHour.getHourlyRate());

            incomeByMechanicId.put(
                    mechanicId,
                    incomeByMechanicId.getOrDefault(mechanicId, BigDecimal.ZERO).add(income)
            );

            hoursByMechanicId.put(
                    mechanicId,
                    hoursByMechanicId.getOrDefault(mechanicId, BigDecimal.ZERO).add(workHour.getHours())
            );
        }

        // 构建技师收入详情
        List<Map<String, Object>> mechanicIncomes = new ArrayList<>();
        for (User mechanic : mechanics) {
            Map<String, Object> income = new HashMap<>();
            income.put("mechanicId", mechanic.getId());
            income.put("mechanicName", mechanic.getFullName() != null ? mechanic.getFullName() : mechanic.getUsername());
            income.put("specialty", mechanic.getSpecialty());
            income.put("hourlyRate", mechanic.getHourlyRate());

            BigDecimal totalIncome = incomeByMechanicId.getOrDefault(mechanic.getId(), BigDecimal.ZERO);
            BigDecimal totalHours = hoursByMechanicId.getOrDefault(mechanic.getId(), BigDecimal.ZERO);

            income.put("totalIncome", totalIncome);
            income.put("totalHours", totalHours);
            income.put("avgHourlyEarning", totalHours.compareTo(BigDecimal.ZERO) > 0
                    ? totalIncome.divide(totalHours, 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO);

            mechanicIncomes.add(income);
        }

        // 按专业统计平均收入
        Map<Specialty, BigDecimal> avgIncomeBySpecialty = new HashMap<>();
        Map<Specialty, BigDecimal> totalIncomeBySpecialty = new HashMap<>();
        Map<Specialty, Integer> countBySpecialty = new HashMap<>();

        for (User mechanic : mechanics) {
            if (mechanic.getSpecialty() != null && incomeByMechanicId.containsKey(mechanic.getId())) {
                BigDecimal income = incomeByMechanicId.get(mechanic.getId());
                Specialty specialty = mechanic.getSpecialty();

                totalIncomeBySpecialty.put(
                        specialty,
                        totalIncomeBySpecialty.getOrDefault(specialty, BigDecimal.ZERO).add(income)
                );

                countBySpecialty.put(
                        specialty,
                        countBySpecialty.getOrDefault(specialty, 0) + 1
                );
            }
        }

        for (Specialty specialty : totalIncomeBySpecialty.keySet()) {
            BigDecimal totalIncome = totalIncomeBySpecialty.get(specialty);
            int count = countBySpecialty.get(specialty);

            if (count > 0) {
                avgIncomeBySpecialty.put(
                        specialty,
                        totalIncome.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP)
                );
            }
        }

        // 计算总收入和平均每小时收入
        BigDecimal totalIncome = incomeByMechanicId.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHours = hoursByMechanicId.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgHourlyRate = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? totalIncome.divide(totalHours, 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        // 按月统计收入趋势
        Map<String, BigDecimal> incomeByMonth = new HashMap<>();

        for (WorkHour workHour : workHours) {
            if (workHour.getWorkOrder().getCompletedAt() != null) {
                String month = workHour.getWorkOrder().getCompletedAt().toString().substring(0, 7); // YYYY-MM
                BigDecimal income = workHour.getHours().multiply(workHour.getHourlyRate());

                incomeByMonth.put(
                        month,
                        incomeByMonth.getOrDefault(month, BigDecimal.ZERO).add(income)
                );
            }
        }

        statistics.put("totalIncome", totalIncome);
        statistics.put("totalWorkHours", totalHours);
        statistics.put("avgHourlyRate", avgHourlyRate);
        statistics.put("mechanicIncomes", mechanicIncomes);
        statistics.put("avgIncomeBySpecialty", avgIncomeBySpecialty);
        statistics.put("incomeByMonth", incomeByMonth);

        return statistics;
    }
}
