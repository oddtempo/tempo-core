package com.tempo.core.shared.domain.rule;

/**
 * Interface for domain business rules.
 * <p>
 * Business rules encapsulate specific validation logic that must be true
 * for the domain to remain in a valid state.
 * </p>
 */
public interface BusinessRule {

    /**
     * Checks if the rule is broken.
     * 
     * @return true if the rule is violated, false otherwise
     */
    boolean isBroken();

    /**
     * Gets the error message to be displayed when the rule is broken.
     * 
     * @return the error message
     */
    String getMessage();

    /**
     * Gets the unique code for this rule, useful for i18n and clients.
     * 
     * @return the rule code or error code
     */
    default String getCode() {
        return this.getClass().getSimpleName();
    }
}
