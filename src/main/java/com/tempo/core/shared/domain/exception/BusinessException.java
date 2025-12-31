package com.tempo.core.shared.domain.exception;

/**
 * Base exception for all business rule violations.
 * <p>
 * Use this when business logic/domain rules are violated.
 * Maps to HTTP 400 Bad Request or 422 Unprocessable Entity.
 * </p>
 * 
 * <p>
 * Example: "Cannot cancel order that has been shipped"
 * </p>
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
