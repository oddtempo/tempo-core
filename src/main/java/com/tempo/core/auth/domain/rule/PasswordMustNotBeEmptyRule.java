package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record PasswordMustNotBeEmptyRule(String passwordHash) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return passwordHash == null || passwordHash.isBlank();
    }

    @Override
    public String getMessage() {
        return "Password is required.";
    }
}
