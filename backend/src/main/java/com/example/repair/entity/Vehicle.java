package com.example.repair.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="vehicles")
@Getter @Setter @NoArgsConstructor
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="owner_id")
    private User owner;
    @Column(nullable=false,unique=true)
    private String plateNo;
    private String model;
    private Short yearMade;
    private String vin;
}

