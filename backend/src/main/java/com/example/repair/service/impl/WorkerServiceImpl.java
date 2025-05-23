package com.example.repair.service.impl;

import com.example.repair.entity.User;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.repository.UserRepository;
import com.example.repair.repository.WorkOrderRepository;
import com.example.repair.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {
    private final UserRepository userRepository;
    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public User getWorkerById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修人员不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getWorkerOrders(Long workerId) {
        return workOrderRepository.findByMechanicsIdAndStatusNot(workerId, WorkOrderStatus.COMPLETED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrder> getWorkerHistory(Long workerId) {
        return workOrderRepository.findByMechanicsIdAndStatus(workerId, WorkOrderStatus.COMPLETED);
    }

    @Override
    @Transactional
    public WorkOrder acceptOrder(Long orderId, Long workerId) {
        WorkOrder order = workOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("维修人员不存在"));

        order.setStatus(WorkOrderStatus.IN_PROGRESS);
        order.getMechanics().add(worker);
        return workOrderRepository.save(order);
    }

    @Override
    @Transactional
    public WorkOrder rejectOrder(Long orderId, Long workerId) {
        WorkOrder order = workOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        order.getMechanics().removeIf(mechanic -> mechanic.getId().equals(workerId));
        if (order.getMechanics().isEmpty()) {
            order.setStatus(WorkOrderStatus.PENDING);
        }
        return workOrderRepository.save(order);
    }
}
