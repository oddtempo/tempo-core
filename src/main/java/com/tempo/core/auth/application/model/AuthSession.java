package com.tempo.core.auth.application.model;

import java.util.Set;
import java.util.UUID;

/**
 * Authentication session stored in Redis.
 */
public record AuthSession(
        UUID userId,
        UUID tenantId,
        String username,
        Set<String> permissions) {
    public static final String TOKEN_PREFIX = "auth:token:";
    public static final int SESSION_TTL_MINUTES = 60 * 24; // 24 hours
}
