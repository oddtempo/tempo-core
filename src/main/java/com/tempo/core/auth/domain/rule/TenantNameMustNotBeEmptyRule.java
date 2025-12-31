package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record TenantNameMustNotBeEmptyRule(String name) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return name == null || name.isBlank();
    }

    @Override
    public String getMessage() {
        return "Tenant name must not be empty.";
    }
}
