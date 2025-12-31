package com.tempo.core.auth.infrastructure.persistence;

import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of {@link UserRepository}.
 * <p>
 * This interface extends both the domain repository (port) and
 * Spring Data JPA repository (adapter) for seamless integration.
 * </p>
 */
@Repository
public interface JpaUserRepository extends UserRepository, JpaRepository<User, UUID> {

    @Override
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.username = :username")
    Optional<User> findByUsernameWithRolesAndPermissions(String username);

    @Override
    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT DISTINCT u FROM User u")
    List<User> findAllWithRoles();
}
