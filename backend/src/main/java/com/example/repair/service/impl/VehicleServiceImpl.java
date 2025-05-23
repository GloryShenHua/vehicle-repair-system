package com.example.repair.service.impl;

import com.example.repair.entity.User;
import com.example.repair.entity.Vehicle;
import com.example.repair.repository.UserRepository;
import com.example.repair.repository.VehicleRepository;
import com.example.repair.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehiclesByUserId(Long userId) {
        return vehicleRepository.findAllByOwnerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id, Long userId) {
        return vehicleRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new RuntimeException("车辆不存在或没有权限"));
    }

    @Override
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        vehicle.setOwner(owner);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicle, Long userId) {
        Vehicle existingVehicle = vehicleRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new RuntimeException("车辆不存在或没有权限"));

        existingVehicle.setPlateNo(vehicle.getPlateNo());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setYearMade(vehicle.getYearMade());
        existingVehicle.setVin(vehicle.getVin());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id, Long userId) {
        Vehicle vehicle = vehicleRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new RuntimeException("车辆不存在或没有权限"));
        vehicleRepository.delete(vehicle);
    }
}
