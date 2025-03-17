package com.banking.repositories.abstracts;

import com.banking.entities.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "WHERE up.user.id = :userId AND up.active = true AND p.active = true")
    List<UserPermission> findActivePermissionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "WHERE up.user.id = :userId AND p.name = :permissionName " +
           "AND up.active = true AND p.active = true")
    Optional<UserPermission> findByUserIdAndPermissionName(
            @Param("userId") Long userId,
            @Param("permissionName") String permissionName);
    
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPermission up " +
           "JOIN up.permission p " +
           "WHERE up.user.id = :userId AND p.resource = :resource AND p.action = :action " +
           "AND up.active = true AND p.active = true")
    boolean hasPermission(
            @Param("userId") Long userId,
            @Param("resource") String resource,
            @Param("action") String action);
    
    void deleteByUserIdAndPermissionId(Long userId, Long permissionId);
} 