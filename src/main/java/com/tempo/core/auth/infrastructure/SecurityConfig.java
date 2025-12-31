package com.tempo.core.auth.infrastructure;

import com.tempo.core.shared.infrastructure.tenant.HibernateTenantFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration.
 * <p>
 * Configures stateless authentication using Redis-based Reference Token.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final RedisAuthenticationFilter redisAuthenticationFilter;
    private final LoginRateLimitFilter loginRateLimitFilter;
    private final HibernateTenantFilter hibernateTenantFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        // Disable default user generation by providing an empty UserDetailsService
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (stateless API)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register_store").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated())

                // Filter chain order:
                // 1. LoginRateLimitFilter (rate limiting)
                // 2. RedisAuthenticationFilter (sets TenantContext)
                // 3. HibernateTenantFilter (enables Hibernate Filter)
                .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(redisAuthenticationFilter, LoginRateLimitFilter.class)
                .addFilterAfter(hibernateTenantFilter, RedisAuthenticationFilter.class)

                .build();
    }
}
