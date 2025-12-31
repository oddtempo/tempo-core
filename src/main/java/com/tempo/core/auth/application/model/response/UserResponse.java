package com.tempo.core.auth.application.model.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for User.
 */
public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        boolean active,
        Set<String> roleNames,
        Instant createdAt) {
}
