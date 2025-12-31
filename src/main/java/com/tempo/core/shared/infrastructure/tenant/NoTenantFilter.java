package com.tempo.core.shared.infrastructure.tenant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to bypass tenant filtering for system-level operations.
 * <p>
 * Apply to methods or classes that should query across all tenants:
 * <ul>
 * <li>Login/Authentication flows (before tenant context is established)</li>
 * <li>Cron jobs and background tasks</li>
 * <li>System admin operations</li>
 * </ul>
 * </p>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoTenantFilter {
}
