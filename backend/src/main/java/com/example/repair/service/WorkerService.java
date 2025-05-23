package com.example.repair.service;

import com.example.repair.entity.User;
import com.example.repair.entity.WorkOrder;

import java.util.List;

public interface WorkerService {
    User getWorkerById(Long id);
    List<WorkOrder> getWorkerOrders(Long workerId);
    List<WorkOrder> getWorkerHistory(Long workerId);
    WorkOrder acceptOrder(Long orderId, Long workerId);
    WorkOrder rejectOrder(Long orderId, Long workerId);
}
