package com.tempo.core.shared.domain.exception;

/**
 * Exception for entity not found errors.
 * <p>
 * Use this when a requested resource does not exist.
 * Maps to HTTP 404 Not Found.
 * </p>
 * 
 * <p>
 * Example: "Product with ID 123 not found"
 * </p>
 */
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final Object entityId;

    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s with ID '%s' not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = "Entity";
        this.entityId = null;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getEntityId() {
        return entityId;
    }
}
