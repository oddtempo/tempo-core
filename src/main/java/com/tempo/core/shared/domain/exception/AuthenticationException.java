package com.tempo.core.shared.domain.exception;

/**
 * Exception for authentication failures.
 * <p>
 * Use this when user credentials are invalid or session expired.
 * Maps to HTTP 401 Unauthorized.
 * </p>
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super("AUTH_FAILED", message);
    }

    public AuthenticationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
