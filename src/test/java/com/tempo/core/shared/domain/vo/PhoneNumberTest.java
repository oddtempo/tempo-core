package com.tempo.core.shared.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "0912345678",
            "0123456789",
            "0987654321"
    })
    @DisplayName("Should accept valid local VN phone numbers")
    void validLocalPhones(String phone) {
        PhoneNumber result = PhoneNumber.of(phone);
        assertNotNull(result);
        // Should store normalized (without leading 0)
        assertEquals(phone.substring(1), result.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+84912345678",
            "+84123456789"
    })
    @DisplayName("Should accept valid international VN phone numbers")
    void validInternationalPhones(String phone) {
        PhoneNumber result = PhoneNumber.of(phone);
        assertNotNull(result);
        // Should store normalized (without +84)
        assertEquals(phone.substring(3), result.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "091234567", // 9 digits
            "09123456789", // 11 digits
            "1234567890", // doesn't start with 0
            "+8491234567", // 8 digits after +84
            "+841234567890", // 10 digits after +84
            "abc",
            "",
            "   "
    })
    @DisplayName("Should reject invalid phone numbers")
    void invalidPhones(String phone) {
        assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of(phone));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Should throw on null")
    void nullPhone(String phone) {
        assertThrows(NullPointerException.class, () -> PhoneNumber.of(phone));
    }

    @Test
    @DisplayName("ofNullable should return null for null/blank")
    void ofNullableReturnsNull() {
        assertNull(PhoneNumber.ofNullable(null));
        assertNull(PhoneNumber.ofNullable(""));
        assertNull(PhoneNumber.ofNullable("   "));
    }

    @Test
    @DisplayName("Should format to local format")
    void toLocalFormat() {
        PhoneNumber phone = PhoneNumber.of("0912345678");
        assertEquals("0912345678", phone.toLocalFormat());
    }

    @Test
    @DisplayName("Should format to international format")
    void toInternationalFormat() {
        PhoneNumber phone = PhoneNumber.of("0912345678");
        assertEquals("+84912345678", phone.toInternationalFormat());
    }

    @Test
    @DisplayName("Should handle phone with spaces/dashes")
    void shouldHandleFormattedPhone() {
        PhoneNumber phone = PhoneNumber.of("091-234-5678");
        assertEquals("912345678", phone.value());

        PhoneNumber phone2 = PhoneNumber.of("0912 345 678");
        assertEquals("912345678", phone2.value());
    }

    @Test
    @DisplayName("toString should return local format")
    void toStringShouldReturnLocalFormat() {
        PhoneNumber phone = PhoneNumber.of("+84912345678");
        assertEquals("0912345678", phone.toString());
    }
}
