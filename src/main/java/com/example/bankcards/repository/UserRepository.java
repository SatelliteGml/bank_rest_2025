package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(Long id, Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
