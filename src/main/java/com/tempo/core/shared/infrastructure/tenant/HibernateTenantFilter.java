package com.tempo.core.shared.infrastructure.tenant;

import com.tempo.core.shared.infrastructure.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP Filter to enable Hibernate tenant filter for each request.
 * <p>
 * This filter enables the Hibernate "tenantFilter" after the TenantContext
 * has been populated by the authentication filter. It ensures all database
 * queries automatically include the tenant (tenant_id) constraint.
 * </p>
 * 
 * <h2>Order:</h2>
 * Must run AFTER RedisAuthenticationFilter (which sets TenantContext).
 * Set to Order(3) since RedisAuthenticationFilter should be Order(1-2).
 */
@Slf4j
@Component
@Order(3)
public class HibernateTenantFilter extends OncePerRequestFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        UUID tenantId = SecurityUtils.getCurrentTenantIdSafe();

        if (tenantId != null) {
            try {
                Session session = entityManager.unwrap(Session.class);
                session.enableFilter("tenantFilter")
                        .setParameter("tenantId", tenantId);
                log.debug("HibernateTenantFilter: Enabled tenantFilter for tenant: {}", tenantId);
            } catch (Exception e) {
                log.warn("Failed to enable tenant filter: {}", e.getMessage());
            }
        } else {
            log.debug("HibernateTenantFilter: No tenant context for request: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
