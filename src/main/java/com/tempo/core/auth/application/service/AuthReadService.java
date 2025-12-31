package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.AuthSession;
import com.tempo.core.auth.application.model.response.PermissionResponse;
import com.tempo.core.auth.application.model.response.RoleResponse;
import com.tempo.core.auth.application.model.response.UserResponse;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.PermissionRepository;
import com.tempo.core.auth.domain.repository.RoleRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthReadService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    public AuthSession getSession(String token) {
        String key = AuthSession.TOKEN_PREFIX + token;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {

            redisTemplate.expire(key, AuthSession.SESSION_TTL_MINUTES, TimeUnit.MINUTES);
            return objectMapper.readValue(json, AuthSession.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse session: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return authMapper.toRoleResponseList(roleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getUserRoles(UUID tenantId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (!user.getTenantId().equals(tenantId)) {
            throw new EntityNotFoundException("User", userId);
        }

        return authMapper.toRoleResponseList(user.getRoles().stream().toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return authMapper.toUserResponseList(userRepository.findAllWithRoles());
    }
}
