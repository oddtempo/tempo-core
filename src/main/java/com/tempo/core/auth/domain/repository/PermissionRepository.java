package com.tempo.core.auth.domain.repository;

import com.tempo.core.auth.domain.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    Optional<Permission> findById(String code);

    List<Permission> findAll();

    List<Permission> findAllById(Iterable<String> codes);

    boolean existsById(String code);

    Permission save(Permission permission);

    void delete(Permission permission);

    void deleteById(String code);
}
