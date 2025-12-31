package com.tempo.core.auth.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tempo.core.shared.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;


public record TenantCreatedEvent(
        @JsonProperty("tenant_id") UUID tenantId,
        @JsonProperty("admin_user_id") UUID adminUserId,
        @JsonProperty("occurred_on") Instant occurredOn) implements DomainEvent {

    public TenantCreatedEvent(UUID tenantId, UUID adminUserId) {
        this(tenantId, adminUserId, Instant.now());
    }

    @Override
    public String aggregateId() {
        return tenantId.toString();
    }
}
