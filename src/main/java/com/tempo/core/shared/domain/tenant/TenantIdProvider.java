package com.tempo.core.shared.domain.tenant;

import java.util.UUID;

/**
 * Domain interface for providing the current tenant ID.
 * <p>
 * This abstraction allows domain entities to access tenant context
 * without depending on infrastructure layer (SecurityUtils, TenantContext).
 * </p>
 *
 * <p>
 * Implementation is provided by infrastructure layer and registered
 * via {@link TenantIdProviderHolder}.
 * </p>
 */
public interface TenantIdProvider {

    /**
     * Gets the current tenant (store) ID.
     *
     * @return the current store ID, or null if not in tenant context
     */
    UUID getCurrentTenantId();

    /**
     * Checks if currently in a tenant context.
     *
     * @return true if tenant context is set
     */
    default boolean isInTenantContext() {
        return getCurrentTenantId() != null;
    }
}
