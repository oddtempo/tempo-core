package com.tempo.core.auth.domain.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionCodeFormatRuleTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "product:create",
            "user:view",
            "inventory-item:adjust",
            "order:123",
            "a:b"
    })
    @DisplayName("Should be valid for correct formats")
    void validFormats(String code) {
        PermissionCodeFormatRule rule = new PermissionCodeFormatRule(code);
        assertFalse(rule.isBroken(), "Rule should NOT be broken for: " + code);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "productcreate",
            "user:",
            ":view",
            "order:view:extra",
            "order: view",
            " order:view",
            "",
            " "
    })
    @DisplayName("Should be broken for invalid formats")
    void invalidFormats(String code) {
        PermissionCodeFormatRule rule = new PermissionCodeFormatRule(code);
        assertTrue(rule.isBroken(), "Rule should be broken for: " + code);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PRODUCT:CREATE",
            "User:View"
    })
    @DisplayName("Should handle case insensitivity correctly (since it calls toLowerCase in rule)")
    void caseInsensitivity(String code) {
        PermissionCodeFormatRule rule = new PermissionCodeFormatRule(code);
        assertFalse(rule.isBroken(), "Rule should NOT be broken for case mix: " + code);
    }
}
