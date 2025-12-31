package com.tempo.core.shared.domain.entity;

import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

/**
 * Base class for tenant-aware simple/child entities.
 * <p>
 * Use this for entities that:
 * <ul>
 * <li>Don't need domain event support</li>
 * <li>Are configuration or child entities</li>
 * <li>Examples: Role, Config, OrderLine</li>
 * </ul>
 * </p>
 * 
 * <h2>Inherits from AbstractTenantEntity:</h2>
 * <ul>
 * <li>Hibernate Filter for read isolation</li>
 * <li>storeId with write protection</li>
 * </ul>
 * 
 * @param <ID> the type of the entity identifier
 * @see AbstractTenantEntity
 * @see TenantAggregateRoot
 */
@MappedSuperclass
public abstract class TenantSimpleEntity<ID extends Serializable> extends AbstractTenantEntity<ID> {
    // Marker class - inherits all tenant protection from AbstractTenantEntity
    // Add child-specific logic here if needed in the future
}
