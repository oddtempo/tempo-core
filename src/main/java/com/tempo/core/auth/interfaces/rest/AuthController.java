package com.tempo.core.auth.interfaces.rest;

import com.tempo.core.auth.application.model.request.AddUserRequest;
import com.tempo.core.auth.application.model.request.CreatePermissionRequest;
import com.tempo.core.auth.application.model.request.CreateRoleRequest;
import com.tempo.core.auth.application.model.request.UpdateUserRolesRequest;
import com.tempo.core.auth.application.model.request.LoginRequest;
import com.tempo.core.auth.application.model.request.RegisterTenantRequest;
import com.tempo.core.auth.application.model.response.LoginResponse;
import com.tempo.core.auth.application.model.response.PermissionResponse;
import com.tempo.core.auth.application.model.response.RoleResponse;
import com.tempo.core.auth.application.model.response.UserResponse;
import com.tempo.core.auth.application.service.AuthReadService;
import com.tempo.core.auth.application.service.AuthWriteService;
import com.tempo.core.shared.infrastructure.tenant.TenantId;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthWriteService authWriteService;
    private final AuthReadService authReadService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authWriteService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authWriteService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(authReadService.getAllRoles());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authReadService.getAllUsers());
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<List<RoleResponse>> getUserRoles(@TenantId UUID tenantId, @PathVariable UUID userId) {
        return ResponseEntity.ok(authReadService.getUserRoles(tenantId, userId));
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Void> addUser(@TenantId UUID tenantId, @Valid @RequestBody AddUserRequest request) {
        authWriteService.addUserToTenant(tenantId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Void> updateUserRoles(
            @TenantId UUID tenantId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        authWriteService.updateUserRoles(tenantId, userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<Void> createRole(@TenantId UUID tenantId, @Valid @RequestBody CreateRoleRequest request) {
        authWriteService.createRole(tenantId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<Void> updateRole(
            @TenantId UUID tenantId,
            @PathVariable UUID roleId,
            @Valid @RequestBody CreateRoleRequest request) {
        authWriteService.updateRole(tenantId, roleId, request);
        return ResponseEntity.ok().build();
    }
}
