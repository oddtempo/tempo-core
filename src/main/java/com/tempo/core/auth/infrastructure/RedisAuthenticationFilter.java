package com.tempo.core.auth.infrastructure;

import com.tempo.core.auth.application.model.AuthSession;
import com.tempo.core.auth.application.service.AuthReadService;
import com.tempo.core.shared.infrastructure.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Redis-based authentication filter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthReadService authReadService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            if (token != null) {
                AuthSession session = authReadService.getSession(token);

                if (session != null) {
                    // 1. Convert permissions to Spring Security authorities
                    List<SimpleGrantedAuthority> authorities = session.permissions().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    // 2. Create authentication object
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            session.userId(),
                            null,
                            authorities);

                    // 3. Set details (can access via SecurityContext later)
                    authentication.setDetails(session);

                    // 4. Set SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 5. Set TenantContext for Hibernate Filter
                    TenantContext.setCurrentTenantId(session.tenantId());

                    log.debug("Authenticated user: {} (tenant: {})", session.username(), session.tenantId());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clear tenant context after request
            TenantContext.clear();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
