package com.tempo.core.shared.domain;

import java.util.UUID;

/**
 * System-wide tenant constants.
 */
public final class TenantConstants {

    /**
     * Special tenant ID used for system-level operations (not belonging to any
     * tenant).
     */
    public static final UUID SYSTEM_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private TenantConstants() {
        // Utility class
    }
}
