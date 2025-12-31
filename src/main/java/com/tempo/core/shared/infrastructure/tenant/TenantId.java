package com.tempo.core.shared.infrastructure.tenant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically resolve the current tenant ID from the security
 * context/session.
 * <p>
 * Use this on controller method parameters of type {@link java.util.UUID}.
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantId {
}
