package com.tempo.core.shared.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.org",
            "user+tag@example.co.uk",
            "USER@EXAMPLE.COM",
            "test123@test.io"
    })
    @DisplayName("Should create valid email")
    void validEmails(String email) {
        Email result = Email.of(email);
        assertNotNull(result);
        assertEquals(email.toLowerCase().trim(), result.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "@example.com",
            "test@",
            "test@.com",
            "test@example",
            "",
            "   "
    })
    @DisplayName("Should reject invalid email formats")
    void invalidEmails(String email) {
        assertThrows(IllegalArgumentException.class, () -> Email.of(email));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Should throw on null")
    void nullEmail(String email) {
        assertThrows(NullPointerException.class, () -> Email.of(email));
    }

    @Test
    @DisplayName("ofNullable should return null for null/blank")
    void ofNullableReturnsNull() {
        assertNull(Email.ofNullable(null));
        assertNull(Email.ofNullable(""));
        assertNull(Email.ofNullable("   "));
    }

    @Test
    @DisplayName("ofNullable should create email for valid input")
    void ofNullableCreatesEmail() {
        Email email = Email.ofNullable("test@example.com");
        assertNotNull(email);
        assertEquals("test@example.com", email.value());
    }

    @Test
    @DisplayName("Should normalize to lowercase")
    void shouldNormalizeToLowercase() {
        Email email = Email.of("  TEST@EXAMPLE.COM  ");
        assertEquals("test@example.com", email.value());
    }

    @Test
    @DisplayName("toString should return value")
    void toStringShouldReturnValue() {
        Email email = Email.of("test@example.com");
        assertEquals("test@example.com", email.toString());
    }
}
