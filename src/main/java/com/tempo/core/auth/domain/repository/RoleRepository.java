package com.tempo.core.auth.domain.repository;

import com.tempo.core.auth.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Optional<Role> findById(UUID id);

    List<Role> findAll();

    Optional<Role> findByNameAndTenantId(String name, UUID tenantId);

    boolean existsByNameAndTenantId(String name, UUID tenantId);

    boolean existsByName(String name);

    Role save(Role role);
}
