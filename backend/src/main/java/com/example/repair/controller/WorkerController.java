package com.example.repair.controller;

import com.example.repair.entity.User;
import com.example.repair.entity.WorkOrder;
import com.example.repair.security.UserDetailsImpl;
import com.example.repair.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('MECHANIC')")
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping("/api/workers/me")
    public ResponseEntity<User> getCurrentWorker(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(workerService.getWorkerById(userDetails.getId()));
    }

    @GetMapping("/api/workers/me/orders")
    public ResponseEntity<List<WorkOrder>> getWorkerOrders(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(workerService.getWorkerOrders(userDetails.getId()));
    }

    @GetMapping("/api/workers/me/history")
    public ResponseEntity<List<WorkOrder>> getWorkerHistory(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(workerService.getWorkerHistory(userDetails.getId()));
    }

    @PostMapping("/api/orders/{id}/accept")
    public ResponseEntity<WorkOrder> acceptOrder(@PathVariable Long id, Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(workerService.acceptOrder(id, userDetails.getId()));
    }

    @PostMapping("/api/orders/{id}/reject")
    public ResponseEntity<WorkOrder> rejectOrder(@PathVariable Long id, Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(workerService.rejectOrder(id, userDetails.getId()));
    }
}