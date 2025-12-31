package com.tempo.core.shared.infrastructure.persistence;

import com.tempo.core.shared.domain.vo.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * JPA Converter for Money Value Object.
 * <p>
 * Stores as "amount|currencyCode" format in database.
 * Example: "100000|VND"
 * </p>
 */
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, String> {

    private static final String SEPARATOR = "|";

    @Override
    public String convertToDatabaseColumn(Money money) {
        if (money == null) {
            return null;
        }
        return money.amount().toPlainString() + SEPARATOR + money.currency().getCurrencyCode();
    }

    @Override
    public Money convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String[] parts = value.split("\\" + SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Money format: " + value);
        }

        BigDecimal amount = new BigDecimal(parts[0]);
        Currency currency = Currency.getInstance(parts[1]);
        return Money.of(amount, currency);
    }
}
