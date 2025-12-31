package com.tempo.core.shared.domain.tenant;

import java.util.UUID;

/**
 * Static holder for {@link TenantIdProvider}.
 * <p>
 * This pattern is necessary because JPA entities cannot use constructor injection.
 * The provider is set once at application startup by infrastructure layer.
 * </p>
 *
 * <h2>Usage in Domain Entity:</h2>
 * <pre>{@code
 * UUID tenantId = TenantIdProviderHolder.getCurrentTenantId();
 * }</pre>
 *
 * <h2>Registration at Startup (Infrastructure):</h2>
 * <pre>{@code
 * @Configuration
 * public class TenantConfig {
 *     @PostConstruct
 *     public void init() {
 *         TenantIdProviderHolder.setProvider(securityContextTenantIdProvider);
 *     }
 * }
 * }</pre>
 */
public final class TenantIdProviderHolder {

    private static TenantIdProvider provider;

    private TenantIdProviderHolder() {
        // Utility class
    }

    /**
     * Sets the tenant ID provider.
     * Should be called once at application startup.
     *
     * @param tenantIdProvider the provider implementation
     */
    public static void setProvider(TenantIdProvider tenantIdProvider) {
        provider = tenantIdProvider;
    }

    /**
     * Gets the current tenant ID from the registered provider.
     *
     * @return the current tenant ID, or null if not in tenant context
     * @throws IllegalStateException if no provider has been registered
     */
    public static UUID getCurrentTenantId() {
        if (provider == null) {
            throw new IllegalStateException(
                    "TenantIdProvider not initialized. Ensure TenantIdProviderHolder.setProvider() " +
                    "is called at application startup.");
        }
        return provider.getCurrentTenantId();
    }

    /**
     * Gets the current tenant ID, returning null if provider not set.
     * Safe version for use during application startup.
     *
     * @return the current tenant ID, or null
     */
    public static UUID getCurrentTenantIdSafe() {
        if (provider == null) {
            return null;
        }
        return provider.getCurrentTenantId();
    }

    /**
     * Checks if currently in a tenant context.
     *
     * @return true if tenant context is set
     */
    public static boolean isInTenantContext() {
        return provider != null && provider.isInTenantContext();
    }
}
