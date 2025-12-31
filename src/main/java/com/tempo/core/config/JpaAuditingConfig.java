package com.tempo.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing configuration.
 * <p>
 * Automatically populates {@code createdAt}, {@code updatedAt},
 * {@code createdBy},
 * {@code updatedBy} fields in entities extending
 * {@link com.tempo.core.shared.domain.entity.BaseEntity}.
 * </p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provides the current auditor (username) for JPA auditing.
     * <p>
     * Retrieves the username from Spring Security context.
     * Returns "system" for unauthenticated operations (e.g., scheduled jobs).
     * </p>
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }

            return Optional.of(authentication.getName());
        };
    }
}
