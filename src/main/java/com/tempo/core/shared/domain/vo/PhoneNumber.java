package com.tempo.core.shared.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    // Vietnamese phone: 10 digits starting with 0, or +84 followed by 9 digits
    private static final Pattern VN_LOCAL_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern VN_INTL_PATTERN = Pattern.compile("^\\+84[0-9]{9}$");

    public PhoneNumber {
        Objects.requireNonNull(value, "Phone number cannot be null");
        String cleaned = value.replaceAll("[\\s\\-()]", "");

        if (!isValidFormat(cleaned)) {
            throw new IllegalArgumentException("Invalid Vietnamese phone number: " + value);
        }

        // Normalize to 9 digits (without prefix)
        value = normalize(cleaned);
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    public static PhoneNumber ofNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new PhoneNumber(value);
    }

    private static boolean isValidFormat(String phone) {
        return VN_LOCAL_PATTERN.matcher(phone).matches()
                || VN_INTL_PATTERN.matcher(phone).matches();
    }

    private static String normalize(String phone) {
        if (phone.startsWith("+84")) {
            return phone.substring(3); // Remove +84, keep 9 digits
        }
        if (phone.startsWith("0")) {
            return phone.substring(1); // Remove leading 0, keep 9 digits
        }
        return phone;
    }

    public String toLocalFormat() {
        return "0" + value;
    }

    public String toInternationalFormat() {
        return "+84" + value;
    }

    @Override
    public String toString() {
        return toLocalFormat();
    }
}
