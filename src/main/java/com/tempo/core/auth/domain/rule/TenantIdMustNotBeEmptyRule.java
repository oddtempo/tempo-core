package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

import java.util.UUID;

public record TenantIdMustNotBeEmptyRule(UUID tenantId) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return tenantId == null;
    }

    @Override
    public String getMessage() {
        return "Tenant ID is required.";
    }
}
