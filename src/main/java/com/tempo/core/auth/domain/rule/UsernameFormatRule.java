package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

import java.util.regex.Pattern;

public record UsernameFormatRule(String username) implements BusinessRule {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]{3,50}$");

    @Override
    public boolean isBroken() {
        if (username == null)
            return true;
        return !USERNAME_PATTERN.matcher(username.toLowerCase()).matches();
    }

    @Override
    public String getMessage() {
        return "Username must be 3-50 characters long and contain only letters, numbers, dots, hyphens, and underscores.";
    }
}
