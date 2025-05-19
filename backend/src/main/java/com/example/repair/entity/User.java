package com.example.repair.entity;
import com.example.repair.enums.Role;
import com.example.repair.enums.Specialty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,unique=true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String fullName;
    private String phone;
    private String email;
    @Enumerated(EnumType.STRING)
    private Specialty specialty;
    private BigDecimal hourlyRate;
}
