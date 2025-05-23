package com.example.repair.controller;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;
import com.example.repair.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(adminService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/workers")
    public ResponseEntity<List<User>> getAllWorkers() {
        return ResponseEntity.ok(adminService.getAllWorkers());
    }

    @GetMapping("/workers/{id}")
    public ResponseEntity<User> getWorkerById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getWorkerById(id));
    }

    @PutMapping("/workers/{id}")
    public ResponseEntity<User> updateWorker(@PathVariable Long id, @RequestBody User worker) {
        return ResponseEntity.ok(adminService.updateWorker(id, worker));
    }

    @DeleteMapping("/workers/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        adminService.deleteWorker(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(adminService.getAllVehicles());
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getVehicleById(id));
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(adminService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        adminService.deleteVehicle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<WorkOrder>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<WorkOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getOrderById(id));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<WorkOrder> updateOrder(@PathVariable Long id, @RequestBody WorkOrder order) {
        return ResponseEntity.ok(adminService.updateOrder(id, order));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        adminService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }
}
