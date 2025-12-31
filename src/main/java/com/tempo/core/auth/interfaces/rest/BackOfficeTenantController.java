package com.tempo.core.auth.interfaces.rest;

import com.tempo.core.auth.application.model.request.ProvisionTenantRequest;
import com.tempo.core.auth.application.service.BackOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/tenants")
@RequiredArgsConstructor
public class BackOfficeTenantController {

    private final BackOfficeService backOfficeService;

    @PostMapping
    @PreAuthorize("@securityUtils.isSuperAdmin()")
    public ResponseEntity<Void> provisionTenant(@Valid @RequestBody ProvisionTenantRequest request) {
        backOfficeService.provisionTenant(request);
        return ResponseEntity.ok().build();
    }
}
