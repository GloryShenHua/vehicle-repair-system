package com.example.repair.repository;

import com.example.repair.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByWorkOrderIdIn(List<Long> workOrderIds);
}

