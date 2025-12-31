package com.tempo.core.auth.job;

import com.tempo.core.auth.domain.model.User;
import com.tempo.core.auth.domain.repository.UserRepository;
import com.tempo.core.shared.domain.entity.AbstractTenantEntity;
import com.tempo.core.shared.domain.vo.Email;
import com.tempo.core.shared.infrastructure.tenant.NoTenantFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class SystemBootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.bootstrap.super-admin.username:super_admin}")
    private String adminUsername;

    @Value("${app.security.bootstrap.super-admin.password:}")
    private String adminPassword;

    @Value("${app.security.bootstrap.super-admin.email:admin@tempo.core}")
    private String adminEmail;

    @Override
    @NoTenantFilter
    @Transactional
    public void run(String... args) {
        ensureInitialSuperAdmin();
    }

    private void ensureInitialSuperAdmin() {

        if (!userRepository.existsByUsername(adminUsername)) {
            log.info("No Super Admin found. Bootstrapping initial account...");

            String passwordToUse = adminPassword;
            boolean isRandom = false;
            if (passwordToUse == null || passwordToUse.isBlank()) {
                passwordToUse = java.util.UUID.randomUUID().toString().substring(0, 8);
                isRandom = true;
            }

            User superAdmin = User.create(
                    AbstractTenantEntity.SYSTEM_TENANT_ID,
                    adminUsername,
                    passwordEncoder.encode(passwordToUse),
                    "System Super Admin");

            superAdmin.setEmail(Email.ofNullable(adminEmail));

            userRepository.save(superAdmin);

            log.info("************************************************************");
            log.info("Initial Super Admin created!");
            log.info("Username: {}", adminUsername);
            if (isRandom) {
                log.info("Password: {} (PLEASE SAVE THIS NOW)", passwordToUse);
            } else {
                log.info("Password: [Configured in Environment]");
            }
            log.info("************************************************************");
        } else {
            log.debug("Super Admin account already exists.");
        }
    }
}
