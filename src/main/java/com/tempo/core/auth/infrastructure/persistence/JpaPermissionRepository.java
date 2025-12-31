package com.tempo.core.auth.infrastructure.persistence;

import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.repository.PermissionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of {@link PermissionRepository}.
 * <p>
 * This interface extends both the domain repository (port) and
 * Spring Data JPA repository (adapter) for seamless integration.
 * </p>
 */
@Repository
public interface JpaPermissionRepository extends PermissionRepository, JpaRepository<Permission, String> {
}
