package com.example.repair.repository;

import com.example.repair.entity.User;
import com.example.repair.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByRole(Role role);
    long countByRole(Role role);
}