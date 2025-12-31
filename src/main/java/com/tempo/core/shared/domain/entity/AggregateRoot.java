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

/**
 * Base class for Aggregate Roots in DDD.
 * <p>
 * Extends {@link BaseEntity} and adds domain event support using Spring Data's
 * {@literal @DomainEvents} mechanism for automatic event publication on save.
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 * <li>Automatic event publishing via Spring Data's save() method</li>
 * <li>Integration with Spring Modulith's event_publication outbox table</li>
 * <li>Type-safe event registration (only accepts DomainEvent)</li>
 * </ul>
 * 
 * <h2>Usage in Aggregate:</h2>
 * 
 * <pre>{@code
 * public class Product extends AggregateRoot<Long> {
 * 
 *     public static Product create(String name, BigDecimal price) {
 *         Product product = new Product(name, price);
 *         product.registerEvent(new ProductCreatedEvent(product.getId(), name, price));
 *         return product;
 *     }
 * }
 * }</pre>
 * 
 * <h2>Usage in Application Service:</h2>
 * 
 * <pre>{@code
 * @Transactional
 * public Long createProduct(CreateProductCommand cmd) {
 *     Product product = Product.create(cmd.name(), cmd.price());
 *     productRepository.save(product);
 *     // Events are automatically published by Spring Data!
 *     return product.getId();
 * }
 * }</pre>
 * 
 * <h2>Event Flow with Spring Modulith:</h2>
 * <ol>
 * <li>Aggregate calls {@code registerEvent(event)}</li>
 * <li>Repository's {@code save()} is called</li>
 * <li>Spring Data invokes {@code @DomainEvents} method</li>
 * <li>Spring Modulith intercepts and saves to event_publication table</li>
 * <li>Transaction commits</li>
 * <li>{@code @AfterDomainEventPublication} clears the events list</li>
 * <li>Spring Modulith dispatches events to listeners</li>
 * </ol>
 * 
 * @param <ID> the type of the aggregate identifier
 */
@MappedSuperclass
public abstract class AggregateRoot<ID extends Serializable> extends BaseDomainEntity<ID> {

    @Transient
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Register a domain event to be published when this aggregate is saved.
     * <p>
     * Call this method from domain logic when a significant state change occurs.
     * Events will be automatically published by Spring Data after {@code save()}.
     * </p>
     * 
     * @param event the domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * Returns all registered domain events.
     * <p>
     * This method is invoked by Spring Data after {@code save()} to collect
     * events for publication. Do not call this method directly.
     * </p>
     * 
     * @return unmodifiable collection of domain events
     */
    @DomainEvents
    protected Collection<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    /**
     * Clears all registered domain events.
     * <p>
     * Automatically invoked by Spring Data after events have been published.
     * Do not call this method directly.
     * </p>
     */
    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
