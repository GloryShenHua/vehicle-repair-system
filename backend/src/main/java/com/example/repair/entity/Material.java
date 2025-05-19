package com.example.repair.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor
public class Material {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    private WorkOrder workOrder;
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
}

