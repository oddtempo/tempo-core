package com.tempo.core.product.domain.event;

import com.tempo.core.shared.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published when a new product is created.
 * <p>
 * Other modules (Inventory, Order, Notification) can listen to this event
 * using {@code @ApplicationModuleListener}.
 * </p>
 * 
 * <h2>Example Listener in Inventory Module:</h2>
 * 
 * <pre>{@code
 * @ApplicationModuleListener
 * void onProductCreated(ProductCreatedEvent event) {
 *     inventoryService.initializeStock(event.aggregateId(), 0);
 * }
 * }</pre>
 */
public record ProductCreatedEvent(
        String aggregateId,
        Instant occurredOn,
        String productName,
        BigDecimal price) implements DomainEvent {

    /**
     * Convenience constructor with auto-generated timestamp.
     */
    public ProductCreatedEvent(String productId, String productName, BigDecimal price) {
        this(productId, Instant.now(), productName, price);
    }
}
