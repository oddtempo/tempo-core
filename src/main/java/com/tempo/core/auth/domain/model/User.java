package com.tempo.core.auth.domain.model;

import com.tempo.core.auth.domain.rule.PasswordMustNotBeEmptyRule;
import com.tempo.core.auth.domain.rule.TenantIdMustNotBeEmptyRule;
import com.tempo.core.auth.domain.rule.UsernameFormatRule;
import com.tempo.core.auth.domain.rule.UsernameMustNotBeEmptyRule;
import com.tempo.core.shared.domain.entity.AggregateRoot;
import com.tempo.core.shared.domain.entity.Audit;
import com.tempo.core.shared.domain.vo.Email;
import com.tempo.core.shared.infrastructure.persistence.EmailConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tenant_id", "username" })
})
@Filter(name = "tenantFilter")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User extends AggregateRoot<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Setter
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Setter
    private String fullName;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Embedded
    private Audit audit = new Audit();

    public static User create(UUID tenantId, String username, String passwordHash, String fullName) {
        User user = new User();
        user.checkRule(new TenantIdMustNotBeEmptyRule(tenantId));
        user.checkRule(new UsernameMustNotBeEmptyRule(username));
        user.checkRule(new UsernameFormatRule(username));
        user.checkRule(new PasswordMustNotBeEmptyRule(passwordHash));

        user.id = UUID.randomUUID();
        user.tenantId = tenantId;
        user.username = username.trim().toLowerCase();
        user.passwordHash = passwordHash;
        user.fullName = fullName;
        return user;
    }

    public void assignRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void syncRoles(Set<Role> newRoles) {
        if (newRoles == null) {
            this.roles.clear();
            return;
        }
        this.roles.clear();
        this.roles.addAll(newRoles);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public Set<String> getAllPermissions() {
        return roles.stream()
                .flatMap(role -> role.getPermissionCodes().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
