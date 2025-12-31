package com.tempo.core.auth.infrastructure.persistence;

import com.tempo.core.auth.domain.model.Tenant;
import com.tempo.core.auth.domain.repository.TenantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * JPA implementation of {@link TenantRepository}.
 * <p>
 * This interface extends both the domain repository (port) and
 * Spring Data JPA repository (adapter) for seamless integration.
 * </p>
 */
@Repository
public interface JpaTenantRepository extends TenantRepository, JpaRepository<Tenant, UUID> {
}
