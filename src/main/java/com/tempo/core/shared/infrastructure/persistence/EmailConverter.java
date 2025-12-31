package com.tempo.core.shared.infrastructure.persistence;

import com.tempo.core.shared.domain.vo.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter for Email Value Object.
 */
@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return email != null ? email.value() : null;
    }

    @Override
    public Email convertToEntityAttribute(String value) {
        return Email.ofNullable(value);
    }
}
