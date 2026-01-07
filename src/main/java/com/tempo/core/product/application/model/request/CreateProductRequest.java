package com.tempo.core.product.application.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    private String description;

    private java.math.BigDecimal defaultPrice;

    @Valid
    @Size(max = 3, message = "Maximum 3 options allowed")
    private List<OptionRequest> options = new ArrayList<>();

    @Getter
    @Setter
    public static class OptionRequest {
        @NotBlank
        @Size(max = 100)
        private String name;

        @Size(max = 100)
        private List<String> values = new ArrayList<>();
    }
}
