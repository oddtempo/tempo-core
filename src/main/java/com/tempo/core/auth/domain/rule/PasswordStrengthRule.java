package com.tempo.core.auth.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

/**
 * Validates password strength requirements.
 * <p>
 * Requirements:
 * <ul>
 * <li>Minimum 8 characters</li>
 * <li>At least 1 digit</li>
 * <li>At least 1 special character</li>
 * </ul>
 */
public record PasswordStrengthRule(String password) implements BusinessRule {

    private static final int MIN_LENGTH = 8;

    @Override
    public boolean isBroken() {
        if (password == null || password.length() < MIN_LENGTH) {
            return true;
        }
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        return !hasDigit || !hasSpecial;
    }

    @Override
    public String getMessage() {
        return "Password must be at least 8 characters with at least 1 digit and 1 special character.";
    }
}
