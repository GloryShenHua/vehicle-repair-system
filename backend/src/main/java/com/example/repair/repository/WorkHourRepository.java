package com.example.repair.repository;
import com.example.repair.entity.WorkHour;
import org.springframework.data.jpa.repository.*;
import java.math.BigDecimal;

public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {
    @Query("SELECT COALESCE(SUM(w.hours*w.hourlyRate),0) FROM WorkHour w WHERE w.workOrder.id=:oid")
    BigDecimal sumCost(Long oid);
}

