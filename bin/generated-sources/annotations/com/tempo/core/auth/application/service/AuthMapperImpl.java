package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.response.LoginResponse;
import com.tempo.core.auth.application.model.response.PermissionResponse;
import com.tempo.core.auth.application.model.response.RoleResponse;
import com.tempo.core.auth.application.model.response.UserResponse;
import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.model.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-31T15:00:41+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public LoginResponse toLoginResponse(User user, String token, Set<String> permissions, int expiresInMinutes) {
        if ( user == null && token == null && permissions == null ) {
            return null;
        }

        UUID userId = null;
        UUID tenantId = null;
        String username = null;
        String fullName = null;
        if ( user != null ) {
            userId = user.getId();
            tenantId = user.getTenantId();
            username = user.getUsername();
            fullName = user.getFullName();
        }
        String token1 = null;
        token1 = token;
        Set<String> permissions1 = null;
        Set<String> set = permissions;
        if ( set != null ) {
            permissions1 = new LinkedHashSet<String>( set );
        }
        int expiresInMinutes1 = 0;
        expiresInMinutes1 = expiresInMinutes;

        LoginResponse loginResponse = new LoginResponse( token1, userId, tenantId, username, fullName, permissions1, expiresInMinutes1 );

        return loginResponse;
    }

    @Override
    public PermissionResponse toPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        String code = null;
        String description = null;

        code = permission.getCode();
        description = permission.getDescription();

        PermissionResponse permissionResponse = new PermissionResponse( code, description );

        return permissionResponse;
    }

    @Override
    public List<PermissionResponse> toPermissionResponseList(List<Permission> permissions) {
        if ( permissions == null ) {
            return null;
        }

        List<PermissionResponse> list = new ArrayList<PermissionResponse>( permissions.size() );
        for ( Permission permission : permissions ) {
            list.add( toPermissionResponse( permission ) );
        }

        return list;
    }

    @Override
    public RoleResponse toRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        Set<String> permissionCodes = null;
        UUID id = null;
        String name = null;
        String description = null;

        permissionCodes = permissionsToCodeSet( role.getPermissions() );
        id = role.getId();
        name = role.getName();
        description = role.getDescription();

        RoleResponse roleResponse = new RoleResponse( id, name, description, permissionCodes );

        return roleResponse;
    }

    @Override
    public List<RoleResponse> toRoleResponseList(List<Role> roles) {
        if ( roles == null ) {
            return null;
        }

        List<RoleResponse> list = new ArrayList<RoleResponse>( roles.size() );
        for ( Role role : roles ) {
            list.add( toRoleResponse( role ) );
        }

        return list;
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Set<String> roleNames = null;
        String email = null;
        UUID id = null;
        String username = null;
        String fullName = null;
        boolean active = false;
        Instant createdAt = null;

        roleNames = rolesToNameSet( user.getRoles() );
        email = emailToString( user.getEmail() );
        id = user.getId();
        username = user.getUsername();
        fullName = user.getFullName();
        active = user.isActive();
        createdAt = user.getCreatedAt();

        UserResponse userResponse = new UserResponse( id, username, email, fullName, active, roleNames, createdAt );

        return userResponse;
    }

    @Override
    public List<UserResponse> toUserResponseList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( toUserResponse( user ) );
        }

        return list;
    }
}
