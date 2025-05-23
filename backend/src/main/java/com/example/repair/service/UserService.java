package com.example.repair.service;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<Vehicle> getUserVehicles(Long userId);
    List<WorkOrder> getUserOrders(Long userId);
    List<WorkOrder> getUserHistory(Long userId);
}
