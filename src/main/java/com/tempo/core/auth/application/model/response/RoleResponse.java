package com.tempo.core.auth.application.model.response;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for Role.
 */
public record RoleResponse(
        UUID id,
        String name,
        String description,
        Set<String> permissionCodes) {
}
