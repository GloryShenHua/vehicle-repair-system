package com.example.repair.service;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    List<User> getAllWorkers();

    User getWorkerById(Long id);

    User updateWorker(Long id, User worker);

    void deleteWorker(Long id);

    List<Vehicle> getAllVehicles();

    Vehicle getVehicleById(Long id);

    Vehicle updateVehicle(Long id, Vehicle vehicle);

    void deleteVehicle(Long id);

    List<WorkOrder> getAllOrders();

    WorkOrder getOrderById(Long id);

    WorkOrder updateOrder(Long id, WorkOrder order);

    void deleteOrder(Long id);

    Map<String, Object> getStatistics();
}