package com.example.repair.repository;

import com.example.repair.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {
    List<WorkHour> findByWorkOrderIdIn(List<Long> workOrderIds);
    List<WorkHour> findByWorkOrderCompletedAtBetween(Instant startDate, Instant endDate);
}

