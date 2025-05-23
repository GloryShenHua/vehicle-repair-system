package com.example.repair.service.impl;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.Role;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.repository.UserRepository;
import com.example.repair.repository.VehicleRepository;
import com.example.repair.repository.WorkOrderRepository;
import com.example.repair.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByRole(Role.USER);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllWorkers() {
        return userRepository.findAllByRole(Role.MECHANIC);
    }

    @Override
    @Transactional(readOnly = true)
    public User getWorkerById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修人员不存在"));
    }

    @Override
    @Transactional
    public User updateWorker(Long id, User worker) {
        User existingWorker = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修人员不存在"));

        existingWorker.setFullName(worker.getFullName());
        existingWorker.setPhone(worker.getPhone());
        existingWorker.setEmail(worker.getEmail());
        existingWorker.setSpecialty(worker.getSpecialty());
        existingWorker.setHourlyRate(worker.getHourlyRate());

        return userRepository.save(existingWorker);
    }

    @Override
    @Transactional
    public void deleteWorker(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("车辆不存在"));
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("车辆不存在"));

        existingVehicle.setPlateNo(vehicle.getPlateNo());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setYearMade(vehicle.getYearMade());
        existingVehicle.setVin(vehicle.getVin());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getAllOrders() {
        return workOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrder getOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }

    @Override
    @Transactional
    public WorkOrder updateOrder(Long id, WorkOrder order) {
        WorkOrder existingOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工单不存在"));

        existingOrder.setStatus(order.getStatus());
        existingOrder.setFeedback(order.getFeedback());
        existingOrder.setTotalCost(order.getTotalCost());

        if (order.getStatus() == WorkOrderStatus.COMPLETED && existingOrder.getCompletedAt() == null) {
            existingOrder.setCompletedAt(Instant.now());
        }

        return workOrderRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        workOrderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 用户统计
        statistics.put("userCount", userRepository.countByRole(Role.USER));
        statistics.put("mechanicCount", userRepository.countByRole(Role.MECHANIC));

        // 车辆统计
        statistics.put("vehicleCount", vehicleRepository.count());

        // 工单统计
        statistics.put("pendingOrderCount", workOrderRepository.countByStatus(WorkOrderStatus.PENDING));
        statistics.put("inProgressOrderCount", workOrderRepository.countByStatus(WorkOrderStatus.IN_PROGRESS));
        statistics.put("completedOrderCount", workOrderRepository.countByStatus(WorkOrderStatus.COMPLETED));

        // 近7天完成的订单
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        statistics.put("lastWeekCompletedOrders",
                workOrderRepository.countByStatusAndCompletedAtAfter(WorkOrderStatus.COMPLETED, sevenDaysAgo));

        // 收入统计（已完成订单的总价值）
        BigDecimal totalRevenue = workOrderRepository.findByStatus(WorkOrderStatus.COMPLETED).stream()
                .map(WorkOrder::getTotalCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalRevenue", totalRevenue);

        return statistics;
    }
}
