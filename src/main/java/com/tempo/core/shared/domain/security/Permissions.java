package com.tempo.core.shared.domain.security;

/**
 * Centralized permission constants for @PreAuthorize annotations.
 * Each module adds its permissions here.
 */
public final class Permissions {

    private Permissions() {
    }

    // === Auth Module ===
    public static final String USER_READ = "hasAuthority('user:read')";
    public static final String USER_WRITE = "hasAuthority('user:write')";
    public static final String USER_MANAGE = "hasAuthority('user:manage')";
    public static final String ROLE_MANAGE = "hasAuthority('role:manage')";
    public static final String IS_AUTHENTICATED = "isAuthenticated()";

    // === Product Module ===
    public static final String PRODUCT_READ = "hasAuthority('product:read')";
    public static final String PRODUCT_CREATE = "hasAuthority('product:create')";
    public static final String PRODUCT_UPDATE = "hasAuthority('product:update')";
    public static final String PRODUCT_DELETE = "hasAuthority('product:delete')";
}
