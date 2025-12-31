package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record TenantCodeMustNotBeEmptyRule(String code) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return code == null || code.isBlank();
    }

    @Override
    public String getMessage() {
        return "Tenant code must not be empty.";
    }
}
