package com.banking.repositories.abstracts;

import com.banking.entities.Permission;
import com.banking.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByNameAndActiveTrue(String name);
    
    List<Permission> findByRoleAndActiveTrue(Role role);
    
    @Query("SELECT p FROM Permission p WHERE p.role = :role AND p.resource = :resource " +
           "AND p.action = :action AND p.active = true")
    Optional<Permission> findByRoleAndResourceAndAction(
            @Param("role") Role role,
            @Param("resource") String resource,
            @Param("action") String action);
    
    List<Permission> findByResourceAndActiveTrue(String resource);
    
    boolean existsByNameAndActiveTrue(String name);
} 