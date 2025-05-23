package com.example.repair.repository;

import com.example.repair.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByOwnerId(Long ownerId);
    Optional<Vehicle> findByIdAndOwnerId(Long id, Long ownerId);
}