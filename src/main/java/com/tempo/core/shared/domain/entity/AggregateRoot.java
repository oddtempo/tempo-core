package com.tempo.core.shared.domain.entity;

import com.tempo.core.shared.domain.event.DomainEvent;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
public abstract class AggregateRoot<ID extends Serializable> extends BaseEntity<ID> {

    @Transient
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    @DomainEvents
    protected Collection<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
