package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record UsernameMustNotBeEmptyRule(String username) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return username == null || username.isBlank();
    }

    @Override
    public String getMessage() {
        return "Username is required.";
    }
}
