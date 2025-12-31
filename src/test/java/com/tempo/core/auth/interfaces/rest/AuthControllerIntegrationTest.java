package com.tempo.core.auth.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tempo.core.auth.application.model.request.AddUserRequest;
import com.tempo.core.auth.application.service.AuthReadService;
import com.tempo.core.auth.application.service.AuthWriteService;
import com.tempo.core.auth.infrastructure.LoginRateLimitFilter;
import com.tempo.core.auth.infrastructure.RedisAuthenticationFilter;
import com.tempo.core.auth.infrastructure.SecurityConfig;
import com.tempo.core.config.WebConfig;
import com.tempo.core.shared.infrastructure.tenant.HibernateTenantFilter;
import com.tempo.core.shared.infrastructure.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({ WebConfig.class, SecurityConfig.class })
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthWriteService authWriteService;

    @MockitoBean
    private AuthReadService authReadService;

    @MockitoBean
    private RedisAuthenticationFilter redisAuthenticationFilter;

    @MockitoBean
    private LoginRateLimitFilter loginRateLimitFilter;

    @MockitoBean
    private HibernateTenantFilter hibernateTenantFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID tenantId;

    @BeforeEach
    void setUp() throws Exception {
        tenantId = UUID.randomUUID();
        TenantContext.setCurrentTenantId(tenantId);

        // Ensure mock filters call the next filter in the chain
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(redisAuthenticationFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(loginRateLimitFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(hibernateTenantFilter).doFilter(any(), any(), any());
    }

    @Test
    @WithMockUser(authorities = "user:manage")
    @DisplayName("POST /api/auth/users should be accessible by user:manage")
    void addUserSuccess() throws Exception {
        AddUserRequest request = new AddUserRequest();
        request.setUsername("newuser");
        request.setPassword("Password123!");
        request.setEmail("new@example.com");
        request.setFullName("Test User");
        request.setRoleNames(Set.of("MEMBER"));

        mockMvc.perform(post("/api/auth/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authWriteService).addUserToTenant(eq(tenantId), any(AddUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = "other:permission")
    @DisplayName("POST /api/auth/users should return 403 for unauthorized user")
    void addUserForbidden() throws Exception {
        AddUserRequest request = new AddUserRequest();
        request.setUsername("newuser");
        request.setPassword("Password123!");
        request.setEmail("new@example.com");
        request.setFullName("Forbidden User");

        mockMvc.perform(post("/api/auth/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "system:manage")
    @DisplayName("POST /api/auth/permissions should be accessible by system:manage")
    void createPermissionSuccess() throws Exception {
        com.tempo.core.auth.application.model.request.CreatePermissionRequest request = new com.tempo.core.auth.application.model.request.CreatePermissionRequest(
                "prod:test", "Test Desc");

        mockMvc.perform(post("/api/auth/permissions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authWriteService).createPermission(any());
    }
}
