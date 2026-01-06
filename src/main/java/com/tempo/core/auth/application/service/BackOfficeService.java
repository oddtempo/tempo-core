package com.tempo.core.auth.application.service;

import com.tempo.core.auth.application.model.request.ProvisionTenantRequest;
import com.tempo.core.auth.domain.model.Tenant;
import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.TenantRepository;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.vo.Email;
import com.tempo.core.shared.infrastructure.tenant.NoTenantFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@NoTenantFilter
@RequiredArgsConstructor
public class BackOfficeService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantSetupService tenantSetupService;

    @Transactional
    public void provisionTenant(ProvisionTenantRequest request) {

        if (tenantRepository.existsByCode(request.getCode())) {
            throw new BusinessException("DUPLICATE_TENANT_CODE", "Tenant code already exists: " + request.getCode());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("DUPLICATE_USERNAME", "Username already exists: " + request.getUsername());
        }

        Tenant tenant = Tenant.create(request.getCode(), request.getName());
        tenant = tenantRepository.save(tenant);
        UUID tenantId = tenant.getId();

        String passwordHash = passwordEncoder.encode(request.getPassword());
        User adminUser = User.create(
                tenantId,
                request.getUsername(),
                passwordHash,
                request.getFullName());

        adminUser.setEmail(Email.ofNullable(request.getEmail()));
        adminUser = userRepository.save(adminUser);

        tenantSetupService.setupTenant(tenantId, adminUser.getId());
    }
}
