package com.example.repair.controller;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.entity.WorkOrder;
import com.example.repair.security.UserDetailsImpl;
import com.example.repair.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
    }

    @GetMapping("/me/vehicles")
    public ResponseEntity<List<Vehicle>> getUserVehicles(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.getUserVehicles(userDetails.getId()));
    }

    @GetMapping("/me/orders")
    public ResponseEntity<List<WorkOrder>> getUserOrders(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.getUserOrders(userDetails.getId()));
    }

    @GetMapping("/me/history")
    public ResponseEntity<List<WorkOrder>> getUserHistory(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userService.getUserHistory(userDetails.getId()));
    }
}