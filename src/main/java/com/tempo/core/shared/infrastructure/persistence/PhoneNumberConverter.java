package com.tempo.core.shared.infrastructure.persistence;

import com.tempo.core.shared.domain.vo.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for PhoneNumber Value Object.
 * <p>
 * Stores the normalized 9-digit value in the database.
 * </p>
 */
@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {

    @Override
    public String convertToDatabaseColumn(PhoneNumber phone) {
        return phone != null ? phone.value() : null;
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        // Reconstruct with leading 0 for validation
        return PhoneNumber.of("0" + value);
    }
}
