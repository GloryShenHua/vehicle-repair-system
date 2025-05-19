package com.example.repair.service.impl;
import com.example.repair.dto.*;
import com.example.repair.entity.*;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.repository.*;
import com.example.repair.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service @RequiredArgsConstructor @Transactional
public class WorkOrderServiceImpl implements WorkOrderService{
    private final WorkOrderRepository workOrderRepo;
    private final UserRepository userRepo;
    private final VehicleRepository vehicleRepo;
    private final MaterialRepository materialRepo;
    private final WorkHourRepository workHourRepo;
    @Override
    public WorkOrder createOrder(Long userId,Long vehicleId){
        WorkOrder wo=new WorkOrder();
        wo.setUser(userRepo.getReferenceById(userId));
        wo.setVehicle(vehicleRepo.getReferenceById(vehicleId));
        return workOrderRepo.save(wo);
    }
    @Override
    public void updateStatus(Long orderId,WorkOrderStatus status){
        WorkOrder wo=workOrderRepo.findById(orderId).orElseThrow();
        wo.setStatus(status);
        if(status==WorkOrderStatus.COMPLETED) wo.setCompletedAt(Instant.now());
    }
    @Override
    public void addMaterial(Long orderId,MaterialDTO dto){
        Material m=new Material();
        m.setWorkOrder(workOrderRepo.getReferenceById(orderId));
        m.setName(dto.name()); m.setQuantity(dto.quantity()); m.setUnitPrice(dto.unitPrice());
        materialRepo.save(m);
    }
    @Override
    public void addWorkHour(Long orderId,WorkHourDTO dto){
        WorkHour wh=new WorkHour();
        wh.setWorkOrder(workOrderRepo.getReferenceById(orderId));
        wh.setMechanic(userRepo.getReferenceById(dto.mechanicId()));
        wh.setHours(dto.hours()); wh.setHourlyRate(dto.hourlyRate());
        workHourRepo.save(wh);
    }
}
