package com.tempo.core.shared.domain.event;

import java.time.Instant;

/**
 * Marker interface for all Domain Events.
 * <p>
 * Events MUST be implemented as Java Records
 * to ensure immutability and proper serialization with Spring Modulith.
 * </p>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * public record ProductCreatedEvent(
 *         String aggregateId,
 *         String productName,
 *         BigDecimal price) implements DomainEvent {
 * 
 *     public ProductCreatedEvent(String productId, String productName, BigDecimal price) {
 *         this(productId, productName, price);
 *     }
 * }
 * }</pre>
 */
public interface DomainEvent {

    /**
     * ID of the Aggregate that raised this event.
     * Used for event tracing and correlation.
     */
    String aggregateId();

    /**
     * Timestamp when the event occurred.
     * Defaults to current time if not overridden.
     */
    default Instant occurredOn() {
        return Instant.now();
    }
}
