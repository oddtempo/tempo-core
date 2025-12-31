package com.tempo.core.auth.domain.model;

import com.tempo.core.auth.domain.rule.RoleNameMustNotBeEmptyRule;
import com.tempo.core.auth.domain.rule.TenantIdMustNotBeEmptyRule;
import com.tempo.core.shared.domain.entity.TenantSimpleEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tenant_id", "name" })
})
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends TenantSimpleEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_code"))
    private Set<Permission> permissions = new HashSet<>();

    public static Role create(UUID tenantId, String name, String description) {
        Role role = new Role();
        role.checkRule(new TenantIdMustNotBeEmptyRule(tenantId));
        role.checkRule(new RoleNameMustNotBeEmptyRule(name));

        role.setTenantId(tenantId);
        role.setName(name.trim().toUpperCase());
        role.setDescription(description);
        return role;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public void syncPermissions(Set<Permission> newPermissions) {
        this.permissions.clear();
        if (newPermissions != null) {
            this.permissions.addAll(newPermissions);
        }
    }

    public Set<String> getPermissionCodes() {
        Set<String> codes = new HashSet<>();
        for (Permission p : permissions) {
            codes.add(p.getCode());
        }
        return codes;
    }
}
