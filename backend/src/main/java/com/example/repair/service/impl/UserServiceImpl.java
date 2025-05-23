package com.example.repair.service.impl;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.repository.UserRepository;
import com.example.repair.repository.VehicleRepository;
import com.example.repair.repository.WorkOrderRepository;
import com.example.repair.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkOrderRepository workOrderRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public List<Vehicle> getUserVehicles(Long userId) {
        return vehicleRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<WorkOrder> getUserOrders(Long userId) {
        return workOrderRepository.findAllByUserIdAndStatusNot(userId, WorkOrderStatus.COMPLETED);
    }

    @Override
    public List<WorkOrder> getUserHistory(Long userId) {
        return workOrderRepository.findAllByUserIdAndStatus(userId, WorkOrderStatus.COMPLETED);
    }
}