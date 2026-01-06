package com.tempo.core.auth.application.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class ProvisionTenantRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String code;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @Email
    private String email;
}
