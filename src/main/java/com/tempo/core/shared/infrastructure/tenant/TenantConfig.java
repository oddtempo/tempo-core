package com.tempo.core.shared.infrastructure.tenant;

import com.tempo.core.shared.domain.tenant.TenantIdProvider;
import com.tempo.core.shared.domain.tenant.TenantIdProviderHolder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for tenant infrastructure.
 * <p>
 * Registers the {@link TenantIdProvider} implementation with the domain layer's
 * {@link TenantIdProviderHolder} at application startup.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class TenantConfig {

    private final TenantIdProvider tenantIdProvider;

    @PostConstruct
    public void initTenantIdProvider() {
        TenantIdProviderHolder.setProvider(tenantIdProvider);
    }
}
