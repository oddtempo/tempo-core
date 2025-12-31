package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

import java.util.regex.Pattern;

public record TenantCodeFormatRule(String code) implements BusinessRule {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-z0-9-]{3,50}$");

    @Override
    public boolean isBroken() {
        if (code == null)
            return true;
        return !CODE_PATTERN.matcher(code.toLowerCase()).matches();
    }

    @Override
    public String getMessage() {
        return "Tenant code must be 3-50 characters long and contain only letters, numbers, and hyphens.";
    }
}
