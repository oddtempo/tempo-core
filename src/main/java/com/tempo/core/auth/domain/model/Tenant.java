package com.tempo.core.auth.domain.model;

import com.tempo.core.auth.domain.rule.TenantCodeFormatRule;
import com.tempo.core.auth.domain.rule.TenantCodeMustNotBeEmptyRule;
import com.tempo.core.auth.domain.rule.TenantNameMustNotBeEmptyRule;
import com.tempo.core.shared.domain.entity.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant extends AggregateRoot<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public static Tenant create(String code, String name) {
        Tenant tenant = new Tenant();
        tenant.checkRule(new TenantCodeMustNotBeEmptyRule(code));
        tenant.checkRule(new TenantCodeFormatRule(code));
        tenant.checkRule(new TenantNameMustNotBeEmptyRule(name));

        tenant.code = code.trim().toLowerCase();
        tenant.name = name.trim();

        return tenant;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
