package com.tempo.core.shared.infrastructure.tenant;

import com.tempo.core.shared.domain.tenant.TenantIdProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Infrastructure implementation of {@link TenantIdProvider}.
 * <p>
 * Retrieves tenant ID from {@link TenantContext} which is set by
 * the authentication filter during request processing.
 * </p>
 */
@Component
public class TenantContextTenantIdProvider implements TenantIdProvider {

    @Override
    public UUID getCurrentTenantId() {
        return TenantContext.getCurrentTenantId();
    }

    @Override
    public boolean isInTenantContext() {
        return TenantContext.isSet();
    }
}
