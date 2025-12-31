package com.tempo.core.shared.infrastructure.error;

import com.tempo.core.shared.domain.exception.AccessDeniedException;
import com.tempo.core.shared.domain.exception.AuthenticationException;
import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler using RFC 7807 ProblemDetail.
 * <p>
 * Converts exceptions to standardized error responses:
 * </p>
 * <ul>
 * <li>Business errors → 400/422</li>
 * <li>Authentication → 401</li>
 * <li>Access denied → 403</li>
 * <li>Not found → 404</li>
 * <li>Optimistic lock → 409</li>
 * <li>Database errors → 500</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_TYPE_BASE = "https://tempo.com/errors/";

    // ============ BUSINESS ERRORS (400/422) ============

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {} - {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage());
        problem.setType(URI.create(ERROR_TYPE_BASE + "business"));
        problem.setTitle("Business Rule Violation");
        problem.setProperty("errorCode", ex.getErrorCode());

        return problem;
    }

    // ============ VALIDATION ERRORS (400) ============

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation error: {}", fieldErrors);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed");
        problem.setType(URI.create(ERROR_TYPE_BASE + "validation"));
        problem.setTitle("Validation Error");
        problem.setProperty("errors", fieldErrors);

        return problem;
    }

    // ============ AUTHENTICATION (401) ============

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage());
        problem.setType(URI.create(ERROR_TYPE_BASE + "authentication"));
        problem.setTitle("Authentication Failed");

        return problem;
    }

    // ============ ACCESS DENIED (403) ============

    @ExceptionHandler({
            AccessDeniedException.class, // Custom domain exception
            org.springframework.security.access.AccessDeniedException.class,
            org.springframework.security.authorization.AuthorizationDeniedException.class
    })
    public ProblemDetail handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage());
        problem.setType(URI.create(ERROR_TYPE_BASE + "access-denied"));
        problem.setTitle("Access Denied");

        return problem;
    }

    // ============ NOT FOUND (404) ============

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create(ERROR_TYPE_BASE + "not-found"));
        problem.setTitle("Resource Not Found");

        return problem;
    }

    // ============ OPTIMISTIC LOCK CONFLICT (409) ============

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockException(OptimisticLockingFailureException ex,
            HttpServletRequest request) {
        log.warn("Optimistic lock conflict: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The resource was modified by another user. Please refresh and try again.");
        problem.setType(URI.create(ERROR_TYPE_BASE + "conflict"));
        problem.setTitle("Conflict");

        return problem;
    }

    // ============ DATA INTEGRITY (409) ============

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Data constraint violation. Please check your input for duplicates or invalid references.");
        problem.setType(URI.create(ERROR_TYPE_BASE + "data-integrity"));
        problem.setTitle("Data Integrity Violation");

        return problem;
    }

    // ============ DATABASE ERROR (500) ============

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDatabaseException(DataAccessException ex, HttpServletRequest request) {
        log.error("Database error: {}", ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A database error occurred. Please try again later.");
        problem.setType(URI.create(ERROR_TYPE_BASE + "database"));
        problem.setTitle("Database Error");

        return problem;
    }

    // ============ GENERIC ERROR (500) ============

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
        problem.setType(URI.create(ERROR_TYPE_BASE + "internal"));
        problem.setTitle("Internal Server Error");

        return problem;
    }
}
