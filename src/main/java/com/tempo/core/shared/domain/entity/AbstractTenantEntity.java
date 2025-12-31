package com.tempo.core.shared.domain.entity;

import com.tempo.core.shared.domain.tenant.TenantIdProviderHolder;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public abstract class AbstractTenantEntity<ID extends Serializable> extends BaseDomainEntity<ID> {

    public static final UUID SYSTEM_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @PrePersist
    public void prePersistTenant() {
        enforceTenantId();
    }

    @PreUpdate
    public void preUpdateTenant() {
        // tenantId is immutable (updatable=false)
    }

    private void enforceTenantId() {
        UUID currentTenantId = TenantIdProviderHolder.getCurrentTenantIdSafe();

        // Only set if not already manually assigned
        if (this.tenantId == null && currentTenantId != null) {
            this.tenantId = currentTenantId;
        }

        // Final check: tenantId must be set before persisting
        if (this.tenantId == null) {
            throw new IllegalStateException(
                    "Cannot persist tenant entity without tenantId. " +
                            "Context is missing and no ID was manually assigned.");
        }
    }
}
