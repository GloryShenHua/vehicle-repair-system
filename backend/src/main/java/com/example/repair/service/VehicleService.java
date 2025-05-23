package com.example.repair.service;

import com.example.repair.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllVehiclesByUserId(Long userId);

    Vehicle getVehicleById(Long id, Long userId);

    Vehicle createVehicle(Vehicle vehicle, Long userId);

    Vehicle updateVehicle(Long id, Vehicle vehicle, Long userId);

    void deleteVehicle(Long id, Long userId);
}