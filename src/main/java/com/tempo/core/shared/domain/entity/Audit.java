package com.tempo.core.shared.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Embeddable audit fields for entities that need tracking.
 * <p>
 * Usage:
 * 
 * <pre>
 * {
 *     &#64;code
 *     &#64;Entity
 *     public class Product extends AggregateRoot<UUID> {
 *         @Embedded
 *         private Audit audit = new Audit();
 *     }
 * }
 * </pre>
 * </p>
 */
@Embeddable
@Getter
public class Audit {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
