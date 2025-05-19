package com.example.repair.controller;
import com.example.repair.dto.*;
import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.WorkOrderStatus;
import com.example.repair.security.UserDetailsImpl;
import com.example.repair.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/orders") @RequiredArgsConstructor
public class WorkOrderController{
    private final WorkOrderService svc;

    @PostMapping
    public WorkOrder create(@RequestParam Long vehicleId, Authentication auth){
        return svc.createOrder(((UserDetailsImpl)auth.getPrincipal()).getId(),vehicleId);
    }
    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id,@RequestParam WorkOrderStatus status){
        svc.updateStatus(id,status);
    }
    @PostMapping("/{id}/material")
    public void addMaterial(@PathVariable Long id,@RequestBody MaterialDTO dto){
        svc.addMaterial(id,dto);
    }
    @PostMapping("/{id}/hour")
    public void addHour(@PathVariable Long id,@RequestBody WorkHourDTO dto){
        svc.addWorkHour(id,dto);
    }
}