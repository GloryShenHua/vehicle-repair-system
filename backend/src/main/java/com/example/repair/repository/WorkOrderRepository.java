package com.example.repair.repository;
import com.example.repair.entity.WorkOrder;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    @EntityGraph(attributePaths={"vehicle","user"})
    List<WorkOrder> findByUserId(Long userId);
}

