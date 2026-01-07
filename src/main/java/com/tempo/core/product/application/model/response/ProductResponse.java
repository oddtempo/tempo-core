package com.tempo.core.product.application.model.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String title,
        String description,
        List<OptionResponse> options,
        List<VariantResponse> variants) {

    public record OptionResponse(
            UUID id,
            String name,
            List<String> values) {
    }

    public record VariantResponse(
            UUID id,
            String sku,
            BigDecimal price,
            String option1,
            String option2,
            String option3) {
    }
}
