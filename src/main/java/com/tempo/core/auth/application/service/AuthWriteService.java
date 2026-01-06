package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.AuthSession;
import com.tempo.core.auth.application.model.request.AddUserRequest;
import com.tempo.core.auth.application.model.request.LoginRequest;
import com.tempo.core.auth.application.model.request.RegisterTenantRequest;
import com.tempo.core.auth.application.model.request.CreatePermissionRequest;
import com.tempo.core.auth.application.model.request.CreateRoleRequest;
import com.tempo.core.auth.application.model.request.UpdateUserRolesRequest;
import com.tempo.core.auth.application.model.response.LoginResponse;
import com.tempo.core.auth.domain.event.TenantCreatedEvent;
import com.tempo.core.auth.domain.model.Permission;
import com.tempo.core.auth.domain.model.Role;
import com.tempo.core.auth.domain.model.Tenant;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.PermissionRepository;
import com.tempo.core.auth.domain.repository.RoleRepository;
import com.tempo.core.auth.domain.repository.TenantRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.auth.domain.rule.PasswordStrengthRule;
import com.tempo.core.shared.domain.exception.AuthenticationException;
import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import com.tempo.core.shared.domain.vo.Email;
import com.tempo.core.shared.infrastructure.tenant.NoTenantFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthWriteService {

    private static final String USER_TOKENS_PREFIX = "auth:user:";
    private static final String TOKENS_SUFFIX = ":tokens";
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;
    private static final String FAILED_ATTEMPTS_PREFIX = "auth:failed:";

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuthMapper authMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TenantSetupService tenantSetupService;

    @NoTenantFilter
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().toLowerCase();

        if (isAccountLocked(username)) {
            log.warn("Login attempt on locked account: {}", username);
            throw new AuthenticationException("Account is temporarily locked. Try again later.");
        }

        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> {
                    recordFailedAttempt(username);
                    return new AuthenticationException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            recordFailedAttempt(username);
            log.warn("Failed login attempt for user: {}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        if (!user.isActive()) {
            log.warn("Login attempt on disabled account: {}", username);
            throw new AuthenticationException("User account is disabled");
        }

        clearFailedAttempts(username);

        Set<String> permissions = user.getAllPermissions();

        String token = generateSecureToken();

        AuthSession session = new AuthSession(
                user.getId(),
                user.getTenantId(),
                user.getUsername(),
                permissions);

        saveSession(token, session);

        trackUserToken(user.getId(), token);

        return authMapper.toLoginResponse(user, token, permissions, AuthSession.SESSION_TTL_MINUTES);
    }

    public void logout(String token) {
        String key = AuthSession.TOKEN_PREFIX + token;
        String json = redisTemplate.opsForValue().get(key);

        if (json != null) {
            try {
                AuthSession session = objectMapper.readValue(json, AuthSession.class);
                untrackUserToken(session.userId(), token);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse session during logout: {}", e.getMessage());
            }
        }
        redisTemplate.delete(key);
    }

    @Transactional
    public void registerTenant(RegisterTenantRequest request) {
        if (tenantRepository.existsByCode(request.getCode())) {
            throw new BusinessException("DUPLICATE_TENANT_CODE", "Tenant code already exists: " + request.getCode());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("DUPLICATE_USERNAME", "Username already exists: " + request.getUsername());
        }

        Tenant tenant = Tenant.create(request.getCode(), request.getName());
        tenant = tenantRepository.save(tenant);

        validatePasswordStrength(request.getPassword());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        User adminUser = User.create(
                tenant.getId(),
                request.getUsername(),
                passwordHash,
                request.getFullName());

        adminUser.setEmail(Email.ofNullable(request.getEmail()));
        adminUser = userRepository.save(adminUser);

        tenantSetupService.setupTenant(tenant.getId(), adminUser.getId());
    }

    @Transactional
    public void addUserToTenant(UUID tenantId, AddUserRequest request) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));

        if (userRepository.existsByTenantIdAndUsername(tenantId, request.getUsername())) {
            throw new BusinessException("DUPLICATE_USERNAME",
                    "Username already exists in this tenant: " + request.getUsername());
        }

        validatePasswordStrength(request.getPassword());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = User.create(
                tenantId,
                request.getUsername(),
                passwordHash,
                request.getFullName());

        user.setEmail(Email.ofNullable(request.getEmail()));

        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            List<Role> tenantRoles = roleRepository.findAll();
            Set<String> requestedRoles = request.getRoleNames();

            for (Role role : tenantRoles) {
                if (requestedRoles.contains(role.getName())) {
                    user.assignRole(role);
                }
            }
        }

        userRepository.save(user);
        log.info("Added user: {} to tenant: {}", user.getUsername(), tenantId);
    }

    @Transactional
    public void updateUserRoles(UUID tenantId, UUID userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (!user.getTenantId().equals(tenantId)) {
            throw new BusinessException("FORBIDDEN", "User does not belong to this tenant");
        }

        List<Role> tenantRoles = roleRepository.findAll();
        Set<Role> rolesToAssign = tenantRoles.stream()
                .filter(role -> request.getRoleNames().contains(role.getName()))
                .collect(java.util.stream.Collectors.toSet());

        user.syncRoles(rolesToAssign);
        userRepository.save(user);

        invalidateAllSessions(userId);

        log.info("Updated roles for user: {} in tenant: {}. Total roles: {}",
                user.getUsername(), tenantId, rolesToAssign.size());
    }

    public void invalidateAllSessions(UUID userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId + TOKENS_SUFFIX;
        Set<String> tokens = redisTemplate.opsForSet().members(userTokensKey);

        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                redisTemplate.delete(AuthSession.TOKEN_PREFIX + token);
            }
            redisTemplate.delete(userTokensKey);
        }
    }

    @Transactional
    public void createRole(UUID tenantId, CreateRoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new BusinessException("DUPLICATE_ROLE", "Role name already exists: " + request.getName());
        }

        Role role = Role.create(tenantId, request.getName(), request.getDescription());

        if (request.getPermissionCodes() != null && !request.getPermissionCodes().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionCodes());
            permissions.forEach(role::addPermission);
        }

        roleRepository.save(role);
        log.info("Created role: {} in tenant: {}", request.getName(), tenantId);
    }

    @Transactional
    public void updateRole(UUID tenantId, UUID roleId, CreateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role", roleId));

        if (!role.getTenantId().equals(tenantId)) {
            throw new BusinessException("FORBIDDEN", "Role does not belong to this tenant");
        }

        if (!role.getName().equalsIgnoreCase(request.getName())) {
            if (roleRepository.existsByName(request.getName())) {
                throw new BusinessException("DUPLICATE_ROLE", "Role name already exists: " + request.getName());
            }
            role.setName(request.getName().trim().toUpperCase());
        }
        role.setDescription(request.getDescription());

        if (request.getPermissionCodes() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionCodes());
            role.syncPermissions(new java.util.HashSet<>(permissions));
        }

        roleRepository.save(role);

        invalidateSessionsForRole(roleId);

        log.info("Updated role: {} in tenant: {}", role.getName(), tenantId);
    }

    private void invalidateSessionsForRole(UUID roleId) {
        List<User> users = userRepository.findByRolesId(roleId);
        users.forEach(user -> invalidateAllSessions(user.getId()));
    }

    @Transactional
    public void createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsById(request.getCode().trim().toLowerCase())) {
            throw new BusinessException("DUPLICATE_PERMISSION", "Permission code already exists: " + request.getCode());
        }

        Permission permission = Permission.create(request.getCode(), request.getDescription());
        permissionRepository.save(permission);

        log.info("Created system permission: {}", request.getCode());
    }

    private void recordFailedAttempt(String username) {
        String key = FAILED_ATTEMPTS_PREFIX + username;
        Long attempts = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, LOCKOUT_MINUTES, TimeUnit.MINUTES);

        if (attempts != null && attempts >= MAX_FAILED_ATTEMPTS) {
            log.warn("Account locked due to {} failed attempts: {}", attempts, username);
        }
    }

    private void clearFailedAttempts(String username) {
        String key = FAILED_ATTEMPTS_PREFIX + username;
        redisTemplate.delete(key);
    }

    private boolean isAccountLocked(String username) {
        String key = FAILED_ATTEMPTS_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value) >= MAX_FAILED_ATTEMPTS;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private void saveSession(String token, AuthSession session) {
        String key = AuthSession.TOKEN_PREFIX + token;
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(key, json, AuthSession.SESSION_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new BusinessException("SESSION_ERROR", "Failed to serialize session: " + e.getMessage());
        }
    }

    private void trackUserToken(UUID userId, String token) {
        String userTokensKey = USER_TOKENS_PREFIX + userId + TOKENS_SUFFIX;
        redisTemplate.opsForSet().add(userTokensKey, token);
        redisTemplate.expire(userTokensKey, AuthSession.SESSION_TTL_MINUTES + 5, TimeUnit.MINUTES);
    }

    private void untrackUserToken(UUID userId, String token) {
        String userTokensKey = USER_TOKENS_PREFIX + userId + TOKENS_SUFFIX;
        redisTemplate.opsForSet().remove(userTokensKey, token);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void validatePasswordStrength(String password) {
        PasswordStrengthRule rule = new PasswordStrengthRule(password);
        if (rule.isBroken()) {
            throw new BusinessException(rule.getCode(), rule.getMessage());
        }
    }
}
