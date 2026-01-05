package com.tempo.core.product.domain.rule;

import com.tempo.core.product.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VariantMustBelongToProductRuleTest {

    @Test
    void shouldNotBeBroken_whenProductIsNotNull() {
        Product product = Product.create(UUID.randomUUID(), "Test Product", "Description");
        assertThat(new VariantMustBelongToProductRule(product).isBroken()).isFalse();
    }

    @Test
    void shouldBeBroken_whenProductIsNull() {
        assertThat(new VariantMustBelongToProductRule(null).isBroken()).isTrue();
    }

    @Test
    void shouldReturnCorrectMessage() {
        var rule = new VariantMustBelongToProductRule(null);
        assertThat(rule.getMessage()).contains("product");
    }
}
