package com.tempo.core.product.domain.rule;

import com.tempo.core.product.domain.model.Product;
import com.tempo.core.shared.domain.rule.BusinessRule;

public record VariantMustBelongToProductRule(Product product) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return product == null;
    }

    @Override
    public String getMessage() {
        return "Variant must belong to a valid product.";
    }
}
