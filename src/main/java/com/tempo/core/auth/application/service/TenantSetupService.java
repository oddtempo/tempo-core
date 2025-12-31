package com.tempo.core.auth.application.service;

import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.PermissionRepository;
import com.tempo.core.auth.domain.repository.RoleRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import com.tempo.core.shared.infrastructure.tenant.NoTenantFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@NoTenantFilter
@RequiredArgsConstructor
public class TenantSetupService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void setupTenant(UUID tenantId, UUID adminUserId) {
        log.info("Starting setup for tenant: {} and admin user: {}", tenantId, adminUserId);

        Role adminRole = getOrCreateDefaultRoles(tenantId);

        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", adminUserId));

        if (!adminUser.getRoles().contains(adminRole)) {
            adminUser.assignRole(adminRole);
            userRepository.save(adminUser);
            log.debug("Assigned ADMIN role to user: {}", adminUserId);
        }
    }

    private Role getOrCreateDefaultRoles(UUID tenantId) {
        List<Permission> allPermissions = permissionRepository.findAll();

        Role admin = getOrCreateRole(tenantId, "ADMIN", "Toàn quyền quản lý shop", allPermissions);

        log.debug("Ensured ADMIN role for tenant: {}", tenantId);
        return admin;
    }

    private Role getOrCreateRole(UUID tenantId, String name, String description, List<Permission>... permissionGroups) {
        return roleRepository.findByNameAndTenantId(name, tenantId)
                .orElseGet(() -> {
                    Role role = Role.create(tenantId, name, description);
                    for (List<Permission> group : permissionGroups) {
                        group.forEach(role::addPermission);
                    }
                    log.info("Created role: {} for tenant: {}", name, tenantId);
                    return roleRepository.save(role);
                });
    }
}
