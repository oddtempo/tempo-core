package com.tempo.core.auth.domain.model;

import com.tempo.core.auth.domain.rule.PermissionCodeFormatRule;
import com.tempo.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission extends BaseEntity<String> {

    @Id
    @Column(length = 100)
    private String code;

    @Column(length = 255)
    private String description;

    public static Permission create(String code, String description) {
        Permission permission = new Permission();
        permission.checkRule(new PermissionCodeFormatRule(code));

        permission.code = code.trim().toLowerCase();
        permission.description = description;
        return permission;
    }

    @Override
    public String getId() {
        return this.code;
    }
}
