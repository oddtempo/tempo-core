package com.tempo.core.shared.infrastructure.tenant;

/**
 * Tenant context holder using ThreadLocal.
 * <p>
 * Stores the current store_id for the request, making it available
 * throughout the request lifecycle without passing it explicitly.
 * </p>
 * 
 * <h2>Usage:</h2>
 * 
 * <pre>{@code
 * // In filter/interceptor (set)
 * TenantContext.setCurrentTenantId(tenantId);
 * 
 * // In service/repository (get)
 * UUID tenantId = TenantContext.getCurrentTenantId();
 * 
 * // At request end (clear)
 * TenantContext.clear();
 * }</pre>
 */
public final class TenantContext {

    private static final ThreadLocal<java.util.UUID> currentTenant = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static java.util.UUID getCurrentTenantId() {
        return currentTenant.get();
    }

    public static void setCurrentTenantId(java.util.UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }

    public static boolean isSet() {
        return currentTenant.get() != null;
    }
}
