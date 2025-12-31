package com.tempo.core.shared.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Base class for all JPA entities with built-in:
 * <ul>
 * <li>Optimistic Locking (@Version) - prevents data loss from concurrent
 * updates</li>
 * <li>System Audit fields - for debugging and technical tracing</li>
 * <li>Proper equals/hashCode implementation for JPA</li>
 * </ul>
 * 
 * <p>
 * <b>Note:</b> This is for <em>system audit</em> (who/when).
 * For <em>business audit</em> (old/new values), use a separate audit log
 * mechanism.
 * </p>
 * 
 * @param <ID> the type of the entity identifier
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID extends Serializable> {

    /**
     * Returns the entity's unique identifier.
     * Subclasses must implement this based on their ID strategy (Long, UUID, etc.).
     */
    public abstract ID getId();

    /**
     * Version for Optimistic Locking.
     * <p>
     * MANDATORY for financial/accounting systems to prevent data loss
     * when multiple users edit the same record concurrently.
     * </p>
     */
    @Version
    @Column(name = "version")
    private Long version;

    // ============ SYSTEM AUDIT FIELDS ============

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    // ============ EQUALS & HASHCODE ============

    /**
     * Two entities are equal if they have the same non-null ID.
     * This follows the "business key" pattern for JPA entities.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /**
     * Uses class hashCode to avoid issues with Hibernate lazy loading proxies.
     * See: Vlad Mihalcea's JPA best practices for equals/hashCode.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
