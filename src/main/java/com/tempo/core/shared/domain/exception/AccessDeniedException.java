package com.tempo.core.shared.domain.exception;

/**
 * Exception for access denied / authorization failures.
 * <p>
 * Use this when user lacks permission to perform an action.
 * Maps to HTTP 403 Forbidden.
 * </p>
 */
public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(String message) {
        super("ACCESS_DENIED", message);
    }

    public AccessDeniedException(String resource, String action) {
        super("ACCESS_DENIED",
                String.format("You don't have permission to %s on %s", action, resource));
    }
}
