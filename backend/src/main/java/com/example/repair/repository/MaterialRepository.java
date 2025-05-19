package com.example.repair.repository;
import com.example.repair.entity.Material;
import org.springframework.data.jpa.repository.*;
import java.math.BigDecimal;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query("SELECT COALESCE(SUM(m.quantity*m.unitPrice),0) FROM Material m WHERE m.workOrder.id=:oid")
    BigDecimal sumCost(Long oid);
}

