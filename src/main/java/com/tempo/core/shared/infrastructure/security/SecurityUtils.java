package com.tempo.core.shared.infrastructure.security;

import com.tempo.core.shared.domain.entity.AbstractTenantEntity;
import com.tempo.core.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Security utility class for accessing current user context.
 */
@Component("securityUtils")
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Get current tenant ID safely.
     */
    public static UUID getCurrentTenantIdSafe() {
        return TenantContext.getCurrentTenantId();
    }

    /**
     * Check if currently in a tenant context.
     */
    public static boolean isInTenantContext() {
        return getCurrentTenantIdSafe() != null;
    }

    /**
     * Check if the current user is a Super Admin (belongs to System Tenant).
     */
    public boolean isSuperAdmin() {
        return AbstractTenantEntity.SYSTEM_TENANT_ID.equals(getCurrentTenantIdSafe());
    }
}
