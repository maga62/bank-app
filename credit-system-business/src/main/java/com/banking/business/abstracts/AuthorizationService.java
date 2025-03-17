package com.banking.business.abstracts;

import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.Permission;
import com.banking.entities.enums.Role;

import java.util.List;

public interface AuthorizationService {
    Result hasPermission(Long userId, String resource, String action);
    Result grantPermission(Long userId, Long permissionId, String grantedBy);
    Result revokePermission(Long userId, Long permissionId);
    DataResult<List<Permission>> getUserPermissions(Long userId);
    DataResult<List<Permission>> getPermissionsByRole(Role role);
    Result createPermission(Permission permission);
    Result updatePermission(Permission permission);
    Result deletePermission(Long permissionId);
} 