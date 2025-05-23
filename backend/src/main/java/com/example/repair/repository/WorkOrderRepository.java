package com.example.repair.repository;

import com.example.repair.entity.WorkOrder;
import com.example.repair.enums.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findAllByUserIdAndStatusNot(Long userId, WorkOrderStatus status);
    List<WorkOrder> findAllByUserIdAndStatus(Long userId, WorkOrderStatus status);
    List<WorkOrder> findByMechanicsIdAndStatusNot(Long mechanicId, WorkOrderStatus status);
    List<WorkOrder> findByMechanicsIdAndStatus(Long mechanicId, WorkOrderStatus status);
    List<WorkOrder> findByStatus(WorkOrderStatus status);
    long countByStatus(WorkOrderStatus status);
    long countByStatusAndCompletedAtAfter(WorkOrderStatus status, Instant date);

    // 添加以下新方法
    List<WorkOrder> findByStatusAndCompletedAtBetween(WorkOrderStatus status, Instant startDate, Instant endDate);
    List<WorkOrder> findByFeedbackIsNotNull();
    List<WorkOrder> findByCreatedAtBetween(Instant startDate, Instant endDate);
}
