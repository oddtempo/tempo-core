package com.tempo.core.product.domain.rule;

import com.tempo.core.shared.domain.rule.BusinessRule;

public record MaxThreeOptionsRule(int currentOptionCount) implements BusinessRule {

    private static final int MAX_OPTIONS = 3;

    @Override
    public boolean isBroken() {
        return currentOptionCount >= MAX_OPTIONS;
    }

    @Override
    public String getMessage() {
        return "Product cannot have more than " + MAX_OPTIONS + " options.";
    }
}
