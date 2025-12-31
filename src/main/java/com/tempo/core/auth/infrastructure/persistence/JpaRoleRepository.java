package com.tempo.core.auth.infrastructure.persistence;

import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaRoleRepository extends RoleRepository, JpaRepository<Role, UUID> {
}
