package com.tempo.core.auth.application.model.response;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(
        String token,
        UUID userId,
        UUID tenantId,
        String username,
        String fullName,
        Set<String> permissions,
        int expiresInMinutes) {
}
