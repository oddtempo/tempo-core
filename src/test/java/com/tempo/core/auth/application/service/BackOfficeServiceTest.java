package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.request.ProvisionTenantRequest;
import com.tempo.core.auth.domain.model.Tenant;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.TenantRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackOfficeServiceTest {

    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TenantSetupService tenantSetupService;

    @InjectMocks
    private BackOfficeService backOfficeService;

    @Test
    @DisplayName("provisionTenant should save tenant, user and call setupTenant")
    void provisionTenantSuccess() {
        // Arrange
        ProvisionTenantRequest request = new ProvisionTenantRequest(
                "new-shop", "New Shop", "admin", "password123", "Admin User", "admin@test.com");

        UUID tenantId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        when(tenantRepository.existsByCode(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");

        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
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
            try {
                var idField = User.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(u, adminId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return u;
        });

        // Act
        backOfficeService.provisionTenant(request);

        // Assert
        verify(tenantRepository).save(any(Tenant.class));
        verify(userRepository).save(any(User.class));
        verify(tenantSetupService).setupTenant(tenantId, adminId);
    }

    @Test
    @DisplayName("provisionTenant should throw if tenant code exists")
    void provisionTenantDuplicateCode() {
        ProvisionTenantRequest request = new ProvisionTenantRequest();
        request.setCode("existing");

        when(tenantRepository.existsByCode("existing")).thenReturn(true);

        assertThrows(BusinessException.class, () -> backOfficeService.provisionTenant(request));
    }
}
