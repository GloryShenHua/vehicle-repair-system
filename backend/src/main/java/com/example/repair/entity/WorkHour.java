package com.example.repair.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor
public class WorkHour {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    private WorkOrder workOrder;
    @ManyToOne(fetch=FetchType.LAZY)
    private User mechanic;
    private BigDecimal hours;
    private BigDecimal hourlyRate;
}

