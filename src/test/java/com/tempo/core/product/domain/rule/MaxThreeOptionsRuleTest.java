package com.tempo.core.product.domain.rule;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxThreeOptionsRuleTest {

    @Test
    void shouldNotBeBroken_whenLessThanThreeOptions() {
        assertThat(new MaxThreeOptionsRule(0).isBroken()).isFalse();
        assertThat(new MaxThreeOptionsRule(1).isBroken()).isFalse();
        assertThat(new MaxThreeOptionsRule(2).isBroken()).isFalse();
    }

    @Test
    void shouldBeBroken_whenThreeOrMoreOptions() {
        assertThat(new MaxThreeOptionsRule(3).isBroken()).isTrue();
        assertThat(new MaxThreeOptionsRule(4).isBroken()).isTrue();
    }

    @Test
    void shouldReturnCorrectMessage() {
        var rule = new MaxThreeOptionsRule(3);
        assertThat(rule.getMessage()).contains("3");
    }
}
