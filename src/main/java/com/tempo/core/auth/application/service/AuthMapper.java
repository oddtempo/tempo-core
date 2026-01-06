package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.response.LoginResponse;
import com.tempo.core.auth.application.model.response.PermissionResponse;
import com.tempo.core.auth.application.model.response.RoleResponse;
import com.tempo.core.auth.application.model.response.UserResponse;
import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.shared.domain.vo.Email;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "tenantId", source = "user.tenantId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "permissions", source = "permissions")
    LoginResponse toLoginResponse(User user, String token, Set<String> permissions, int expiresInMinutes);

    PermissionResponse toPermissionResponse(Permission permission);

    List<PermissionResponse> toPermissionResponseList(List<Permission> permissions);

    @Mapping(target = "permissionCodes", source = "permissions", qualifiedByName = "permissionsToCodeSet")
    RoleResponse toRoleResponse(Role role);

    List<RoleResponse> toRoleResponseList(List<Role> roles);

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToNameSet")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "createdAt", source = "audit.createdAt")
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    @Named("permissionsToCodeSet")
    default Set<String> permissionsToCodeSet(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Named("rolesToNameSet")
    default Set<String> rolesToNameSet(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.value() : null;
    }
}
