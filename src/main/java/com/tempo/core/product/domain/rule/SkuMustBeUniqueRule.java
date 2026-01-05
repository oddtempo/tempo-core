package com.tempo.core.product.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record SkuMustBeUniqueRule(String sku) implements BusinessRule {

    private static final int MAX_SKU_LENGTH = 100;

    @Override
    public boolean isBroken() {
        return sku == null || sku.isBlank() || sku.length() > MAX_SKU_LENGTH;
    }

    @Override
    public String getMessage() {
        return "SKU is required and must be at most " + MAX_SKU_LENGTH + " characters.";
    }
}
