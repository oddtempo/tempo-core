package com.tempo.core.auth.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tempo.core.auth.application.model.request.CreatePermissionRequest;
import com.tempo.core.auth.application.model.request.CreateRoleRequest;
import com.tempo.core.auth.application.model.request.UpdateUserRolesRequest;
import com.tempo.core.auth.application.model.request.RegisterTenantRequest;
import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.model.Tenant;
import com.tempo.core.auth.domain.repository.PermissionRepository;
import com.tempo.core.auth.domain.repository.RoleRepository;
import com.tempo.core.auth.domain.repository.TenantRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthWriteServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SetOperations<String, String> setOperations;
    @Mock
    private TenantSetupService tenantSetupService;

    @InjectMocks
    private AuthWriteService authWriteService;

    private UUID tenantId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("updateUserRoles should update roles and invalidate sessions")
    void updateUserRolesSuccess() {
        UpdateUserRolesRequest request = new UpdateUserRolesRequest(Set.of("ADMIN"));
        User user = User.create(tenantId, "user", "pass", "email");

        Role adminRole = Role.create(tenantId, "ADMIN", "Admin Role");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(List.of(adminRole));
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(Collections.emptySet());

        authWriteService.updateUserRoles(tenantId, userId, request);

        verify(userRepository).save(user);
        verify(redisTemplate, atLeastOnce()).opsForSet();
    }

    @Test
    @DisplayName("Should aggregate permissions from all assigned roles")
    void aggregatePermissions() {
        UUID tenantId = UUID.randomUUID();
        User user = User.create(tenantId, "testuser", "password", "test@example.com");

        Role role1 = Role.create(tenantId, "ROLE_1", "Role 1");
        Permission p1 = Permission.create("res1:action1", "Desc 1");
        Permission p2 = Permission.create("res1:action2", "Desc 2");
        role1.addPermission(p1);
        role1.addPermission(p2);

        Role role2 = Role.create(tenantId, "ROLE_2", "Role 2");
        Permission p3 = Permission.create("res2:action1", "Desc 3");
        role2.addPermission(p3);

        user.assignRole(role1);
        user.assignRole(role2);

        Set<String> allPermissions = user.getAllPermissions();
        assertEquals(3, allPermissions.size());
        assertTrue(allPermissions.contains("res1:action1"));
        assertTrue(allPermissions.contains("res1:action2"));
        assertTrue(allPermissions.contains("res2:action1"));
    }

    @Test
    @DisplayName("updateUserRoles should throw EntityNotFoundException if user missing")
    void updateUserRolesUserNotFound() {
        UpdateUserRolesRequest request = new UpdateUserRolesRequest(Set.of("ADMIN"));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authWriteService.updateUserRoles(tenantId, userId, request));
    }

    @Test
    @DisplayName("createRole should save new role")
    void createRoleSuccess() {
        CreateRoleRequest request = new CreateRoleRequest("MEMBER", "Member Role", Set.of("p1:v1"));
        Permission p1 = Permission.create("p1:v1", "D1");

        when(roleRepository.existsByName("MEMBER")).thenReturn(false);
        when(permissionRepository.findAllById(any())).thenReturn(List.of(p1));

        authWriteService.createRole(tenantId, request);

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("createRole should throw BusinessException if role exists")
    void createRoleDuplicate() {
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "D", Set.of());
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authWriteService.createRole(tenantId, request));
    }

    @Test
    @DisplayName("createPermission should save new permission")
    void createPermissionSuccess() {
        CreatePermissionRequest request = new CreatePermissionRequest("test:perm", "Desc");
        when(permissionRepository.existsById("test:perm")).thenReturn(false);

        authWriteService.createPermission(request);

        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    @DisplayName("registerTenant should save tenant, user and publish event")
    void registerTenantSuccess() {
        // Arrange
        RegisterTenantRequest request = new RegisterTenantRequest();
        request.setCode("test-t");
        request.setName("Test Tenant");
        request.setUsername("admin");
        request.setPassword("Password123!");
        request.setFullName("Admin User");
        request.setEmail("admin@test.com");

        when(tenantRepository.existsByCode("test-t")).thenReturn(false);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed-pass");

        // Mock save to return entity with ID (simulate JPA @GeneratedValue)
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            // Use reflection to set ID (simulating JPA behavior)
            try {
                var idField = Tenant.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(t, tenantId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return t;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            // Use reflection to set ID (simulating JPA behavior)
            try {
                var idField = User.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(u, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return u;
        });

        // Act
        authWriteService.registerTenant(request);

        // Assert
        // 1. Verify tenant saved
        ArgumentCaptor<Tenant> tenantCaptor = ArgumentCaptor.forClass(Tenant.class);
        verify(tenantRepository).save(tenantCaptor.capture());
        Tenant savedTenant = tenantCaptor.getValue();
        assertThat(savedTenant.getCode()).isEqualTo("test-t");
        assertThat(savedTenant.getName()).isEqualTo("Test Tenant");

        // 2. Verify user saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("admin");
        assertThat(savedUser.getFullName()).isEqualTo("Admin User");

        // 3. Verify tenant setup service called
        verify(tenantSetupService).setupTenant(tenantId, userId);
    }
}
