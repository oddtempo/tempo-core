package com.tempo.core.auth.infrastructure;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * Rate limiting filter for login endpoint.
 * <p>
 * Protects against brute-force attacks by limiting login attempts
 * per IP address using Token Bucket algorithm backed by Redis.
 * </p>
 * 
 * <p>
 * Default: 10 attempts per minute per IP.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 10;
    private static final int REFILL_MINUTES = 1;
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:login:";

    private final ProxyManager<String> proxyManager;

    private static final Supplier<BucketConfiguration> BUCKET_CONFIG_SUPPLIER = () -> BucketConfiguration.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(MAX_ATTEMPTS)
                    .refillGreedy(MAX_ATTEMPTS, Duration.ofMinutes(REFILL_MINUTES))
                    .build())
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Only rate limit POST /api/auth/login
        if (isLoginRequest(request)) {
            String clientIp = getClientIp(request);
            String bucketKey = RATE_LIMIT_KEY_PREFIX + clientIp;

            var bucket = proxyManager.builder()
                    .build(bucketKey, BUCKET_CONFIG_SUPPLIER);

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {"error": "Too many login attempts. Please try again later."}
                        """);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && LOGIN_PATH.equals(request.getRequestURI());
    }

    /**
     * Get client IP address.
     * <p>
     * When forward-headers-strategy is set to 'framework', Spring's
     * ForwardedHeaderFilter will properly handle X-Forwarded-For headers
     * and set the correct remote address.
     * </p>
     */
    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
