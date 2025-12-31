package com.tempo.core.shared.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Should create VND money")
    void createVndMoney() {
        Money money = Money.ofVnd(100000);
        assertEquals(BigDecimal.valueOf(100000), money.amount());
        assertEquals(Money.VND, money.currency());
    }

    @Test
    @DisplayName("Should create USD money with decimal")
    void createUsdMoney() {
        Money money = Money.ofUsd(new BigDecimal("99.99"));
        assertEquals(new BigDecimal("99.99"), money.amount());
        assertEquals(Money.USD, money.currency());
    }

    @Test
    @DisplayName("Should add money of same currency")
    void addMoney() {
        Money a = Money.ofVnd(100000);
        Money b = Money.ofVnd(50000);
        Money result = a.add(b);

        assertEquals(BigDecimal.valueOf(150000), result.amount());
        assertEquals(Money.VND, result.currency());
    }

    @Test
    @DisplayName("Should subtract money of same currency")
    void subtractMoney() {
        Money a = Money.ofVnd(100000);
        Money b = Money.ofVnd(30000);
        Money result = a.subtract(b);

        assertEquals(BigDecimal.valueOf(70000), result.amount());
    }

    @Test
    @DisplayName("Should multiply money")
    void multiplyMoney() {
        Money money = Money.ofVnd(10000);
        Money result = money.multiply(3);

        assertEquals(BigDecimal.valueOf(30000), result.amount());
    }

    @Test
    @DisplayName("Should throw on currency mismatch for add")
    void currencyMismatchAdd() {
        Money vnd = Money.ofVnd(100000);
        Money usd = Money.ofUsd(new BigDecimal("100"));

        assertThrows(IllegalArgumentException.class, () -> vnd.add(usd));
    }

    @Test
    @DisplayName("Should throw on currency mismatch for subtract")
    void currencyMismatchSubtract() {
        Money vnd = Money.ofVnd(100000);
        Money usd = Money.ofUsd(new BigDecimal("100"));

        assertThrows(IllegalArgumentException.class, () -> vnd.subtract(usd));
    }

    @Test
    @DisplayName("Should compare money values")
    void compareMoney() {
        Money a = Money.ofVnd(100000);
        Money b = Money.ofVnd(50000);

        assertTrue(a.isGreaterThan(b));
        assertTrue(b.isLessThan(a));
        assertFalse(a.isZero());
        assertTrue(a.isPositive());
        assertFalse(a.isNegative());
    }

    @Test
    @DisplayName("Should negate money")
    void negateMoney() {
        Money money = Money.ofVnd(100000);
        Money negated = money.negate();

        assertEquals(BigDecimal.valueOf(-100000), negated.amount());
        assertTrue(negated.isNegative());
    }

    @Test
    @DisplayName("Zero VND should be zero")
    void zeroVnd() {
        assertTrue(Money.ZERO_VND.isZero());
        assertFalse(Money.ZERO_VND.isPositive());
        assertFalse(Money.ZERO_VND.isNegative());
    }

    @Test
    @DisplayName("Should throw on null amount")
    void nullAmount() {
        assertThrows(NullPointerException.class, () -> Money.of(null, Money.VND));
    }

    @Test
    @DisplayName("Should throw on null currency")
    void nullCurrency() {
        assertThrows(NullPointerException.class, () -> Money.of(BigDecimal.TEN, (Currency) null));
    }

    @Test
    @DisplayName("Should create from currency code")
    void createFromCurrencyCode() {
        Money money = Money.of(new BigDecimal("100"), "EUR");
        assertEquals(Currency.getInstance("EUR"), money.currency());
    }

    @Test
    @DisplayName("toString should show amount and currency")
    void toStringShouldShowAmountAndCurrency() {
        Money money = Money.ofVnd(100000);
        assertEquals("100000 VND", money.toString());
    }
}
