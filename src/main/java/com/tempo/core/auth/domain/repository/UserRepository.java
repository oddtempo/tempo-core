package com.tempo.core.auth.domain.repository;

import com.tempo.core.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameWithRolesAndPermissions(String username);

    List<User> findAll();

    List<User> findAllWithRoles();

    List<User> findByRolesId(UUID roleId);

    boolean existsByUsername(String username);

    boolean existsByTenantIdAndUsername(UUID tenantId, String username);

    User save(User user);

    void delete(User user);

    void deleteById(UUID id);
}
