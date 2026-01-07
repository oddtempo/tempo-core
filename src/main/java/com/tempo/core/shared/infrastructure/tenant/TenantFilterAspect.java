package com.tempo.core.shared.infrastructure.tenant;

import com.tempo.core.shared.domain.exception.AuthenticationException;
import com.tempo.core.shared.infrastructure.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Session;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Aspect to automatically enable Hibernate tenant filter for transactional
 * methods.
 * <p>
 * Intercepts all methods annotated with {@code @Transactional} and enables the
 * tenant filter using the current tenant ID from {@link TenantContext}.
 * </p>
 * 
 * <h2>Bypass Mechanism:</h2>
 * Methods or classes annotated with {@link NoTenantFilter} will skip filter
 * activation, allowing system-level operations to query across all tenants.
 * 
 * <h2>Order:</h2>
 * Uses {@code Ordered.LOWEST_PRECEDENCE} to run AFTER the transaction
 * interceptor has established the EntityManager/Session.
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // Run AFTER transaction starts
@Slf4j
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Enable tenant filter around transactional methods.
     * Using @Around ensures the filter is enabled AFTER the transaction
     * and EntityManager/Session have been established.
     * 
     * @param joinPoint the join point for the intercepted method
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@within(org.springframework.transaction.annotation.Transactional)")
    public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. Check Bypass Annotation
        if (shouldBypass(joinPoint)) {
            log.debug("Bypassing tenant filter for: {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // 2. Get current tenant ID
        UUID tenantId = SecurityUtils.getCurrentTenantIdSafe();

        // 3. Fail-fast if tenant context is missing (security enforcement)
        if (tenantId == null) {
            log.error("Tenant context missing for method: {} - this is a security violation",
                    joinPoint.getSignature());
            throw new AuthenticationException("TENANT_CONTEXT_MISSING",
                    "Tenant context is required for this operation. Please authenticate first.");
        }

        // 4. Enable tenant filter
        try {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
            log.debug("Enabled tenantFilter for tenant: {} on method: {}",
                    tenantId, joinPoint.getSignature().getName());
        } catch (Exception e) {
            log.error("Failed to enable tenant filter for tenant {}: {}", tenantId, e.getMessage());
            throw new IllegalStateException("Failed to enable tenant isolation", e);
        }

        // 5. Proceed with the actual method
        return joinPoint.proceed();
    }

    private boolean shouldBypass(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.isAnnotationPresent(NoTenantFilter.class)) {
            return true;
        }

        return joinPoint.getTarget().getClass().isAnnotationPresent(NoTenantFilter.class);
    }
}
