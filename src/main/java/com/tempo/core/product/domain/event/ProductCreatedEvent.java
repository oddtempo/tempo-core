package com.tempo.core.product.domain.event;

import com.tempo.core.shared.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ProductCreatedEvent(
        String aggregateId,
        Instant occurredOn,
        String title) implements DomainEvent {

    public ProductCreatedEvent(UUID productId, String title) {
        this(productId != null ? productId.toString() : null, Instant.now(), title);
    }
}
