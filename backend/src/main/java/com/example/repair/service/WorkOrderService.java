package com.example.repair.service;
import com.example.repair.dto.*;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.WorkOrderStatus;

public interface WorkOrderService {
    WorkOrder createOrder(Long userId,Long vehicleId);
    void updateStatus(Long orderId,WorkOrderStatus status);
    void addMaterial(Long orderId,MaterialDTO dto);
    void addWorkHour(Long orderId,WorkHourDTO dto);
}

