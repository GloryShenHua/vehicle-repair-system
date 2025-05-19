package com.example.repair.entity;
import com.example.repair.enums.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="work_orders")
@Getter @Setter @NoArgsConstructor
public class WorkOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="vehicle_id")
    private Vehicle vehicle;
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status = WorkOrderStatus.PENDING;
    private String feedback;
    private BigDecimal totalCost;
    @CreationTimestamp
    private Instant createdAt;
    private Instant completedAt;
    @ManyToMany
    @JoinTable(name="work_order_mechanics",
            joinColumns=@JoinColumn(name="work_order_id"),
            inverseJoinColumns=@JoinColumn(name="mechanic_id"))
    private Set<User> mechanics = new HashSet<>();
}
