package com.visioners.civic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visioners.civic.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
}
