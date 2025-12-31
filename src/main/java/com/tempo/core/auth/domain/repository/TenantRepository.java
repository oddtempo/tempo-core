package com.tempo.core.auth.domain.repository;

import com.tempo.core.auth.domain.model.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Optional<Tenant> findById(UUID id);

    boolean existsByCode(String code);

    Tenant save(Tenant tenant);
}
