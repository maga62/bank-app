package com.banking.business.concretes;

import com.banking.business.abstracts.AuthorizationService;
import com.banking.core.utilities.results.*;
import com.banking.entities.Permission;
import com.banking.entities.UserPermission;
import com.banking.entities.enums.Role;
import com.banking.repositories.abstracts.PermissionRepository;
import com.banking.repositories.abstracts.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationManager implements AuthorizationService {

    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Override
    @Cacheable(value = "userPermissions", key = "#userId + '_' + #resource + '_' + #action")
    public Result hasPermission(Long userId, String resource, String action) {
        log.info("Checking permission for user {} on resource {} with action {}", userId, resource, action);
        
        boolean hasPermission = userPermissionRepository.hasPermission(userId, resource, action);
        return hasPermission ? 
                new SuccessResult("User has required permission") :
                new ErrorResult("User does not have required permission");
    }

    @Override
    @Transactional
    @CacheEvict(value = "userPermissions", key = "#userId + '*'")
    public Result grantPermission(Long userId, Long permissionId, String grantedBy) {
        log.info("Granting permission {} to user {} by {}", permissionId, userId, grantedBy);

        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isEmpty()) {
            return new ErrorResult("Permission not found");
        }

        UserPermission userPermission = new UserPermission();
        userPermission.setPermission(permissionOpt.get());
        userPermission.setGrantedBy(grantedBy);
        userPermissionRepository.save(userPermission);

        return new SuccessResult("Permission granted successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = "userPermissions", key = "#userId + '*'")
    public Result revokePermission(Long userId, Long permissionId) {
        log.info("Revoking permission {} from user {}", permissionId, userId);

        userPermissionRepository.deleteByUserIdAndPermissionId(userId, permissionId);
        return new SuccessResult("Permission revoked successfully");
    }

    @Override
    @Cacheable(value = "userPermissions", key = "#userId")
    public DataResult<List<Permission>> getUserPermissions(Long userId) {
        log.info("Getting permissions for user {}", userId);

        List<UserPermission> userPermissions = userPermissionRepository.findActivePermissionsByUserId(userId);
        List<Permission> permissions = userPermissions.stream()
                .map(UserPermission::getPermission)
                .toList();

        return new SuccessDataResult<>(permissions, "User permissions retrieved successfully");
    }

    @Override
    @Cacheable(value = "rolePermissions", key = "#role")
    public DataResult<List<Permission>> getPermissionsByRole(Role role) {
        log.info("Getting permissions for role {}", role);

        List<Permission> permissions = permissionRepository.findByRoleAndActiveTrue(role);
        return new SuccessDataResult<>(permissions, "Role permissions retrieved successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"rolePermissions", "userPermissions"}, allEntries = true)
    public Result createPermission(Permission permission) {
        log.info("Creating new permission: {}", permission.getName());

        if (permissionRepository.existsByNameAndActiveTrue(permission.getName())) {
            return new ErrorResult("Permission with this name already exists");
        }

        permissionRepository.save(permission);
        return new SuccessResult("Permission created successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"rolePermissions", "userPermissions"}, allEntries = true)
    public Result updatePermission(Permission permission) {
        log.info("Updating permission: {}", permission.getId());

        Optional<Permission> existingPermissionOpt = permissionRepository.findById(permission.getId());
        if (existingPermissionOpt.isEmpty()) {
            return new ErrorResult("Permission not found");
        }

        permissionRepository.save(permission);
        return new SuccessResult("Permission updated successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"rolePermissions", "userPermissions"}, allEntries = true)
    public Result deletePermission(Long permissionId) {
        log.info("Deleting permission: {}", permissionId);

        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isEmpty()) {
            return new ErrorResult("Permission not found");
        }

        Permission permission = permissionOpt.get();
        permission.setActive(false);
        permissionRepository.save(permission);

        return new SuccessResult("Permission deleted successfully");
    }
} 