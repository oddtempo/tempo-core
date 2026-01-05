package com.tempo.core.product.domain.event;

import com.tempo.core.shared.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ProductVariantCreatedEvent(
        String aggregateId,
        Instant occurredOn,
        UUID variantId,
        String sku) implements DomainEvent {

    public ProductVariantCreatedEvent(UUID variantId, String sku) {
        this(variantId != null ? variantId.toString() : null, Instant.now(), variantId, sku);
    }
}
