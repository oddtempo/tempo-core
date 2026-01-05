package com.tempo.core.product.domain.rule;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SkuMustBeUniqueRuleTest {

    @Test
    void shouldNotBeBroken_whenSkuIsValid() {
        assertThat(new SkuMustBeUniqueRule("SKU-001").isBroken()).isFalse();
        assertThat(new SkuMustBeUniqueRule("PROD-ABC-123").isBroken()).isFalse();
    }

    @Test
    void shouldBeBroken_whenSkuIsNull() {
        assertThat(new SkuMustBeUniqueRule(null).isBroken()).isTrue();
    }

    @Test
    void shouldBeBroken_whenSkuIsBlank() {
        assertThat(new SkuMustBeUniqueRule("").isBroken()).isTrue();
        assertThat(new SkuMustBeUniqueRule("   ").isBroken()).isTrue();
    }

    @Test
    void shouldBeBroken_whenSkuExceedsMaxLength() {
        String longSku = "A".repeat(101);
        assertThat(new SkuMustBeUniqueRule(longSku).isBroken()).isTrue();
    }

    @Test
    void shouldNotBeBroken_whenSkuIsAtMaxLength() {
        String maxSku = "A".repeat(100);
        assertThat(new SkuMustBeUniqueRule(maxSku).isBroken()).isFalse();
    }
}
