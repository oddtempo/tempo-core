package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

import java.util.regex.Pattern;

public record PermissionCodeFormatRule(String code) implements BusinessRule {

    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^[a-z0-9-]+:[a-z0-9-]+$");

    @Override
    public boolean isBroken() {
        if (code == null)
            return true;
        return !PERMISSION_PATTERN.matcher(code.toLowerCase()).matches();
    }

    @Override
    public String getMessage() {
        return "Permission code must follow the 'resource:action' format (e.g., product:create).";
    }
}
