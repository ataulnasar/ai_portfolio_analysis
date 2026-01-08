package com.ai.stocks.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @param ticker optional
 * @param isin   optional
 * @param sector optional
 * @param region optional
 */
public record EquityHolding(String instrumentName, String ticker, String isin, BigDecimal quantity, BigDecimal price,
                            String currency, String sector, String region) {
    public EquityHolding(
            String instrumentName,
            String ticker,
            String isin,
            BigDecimal quantity,
            BigDecimal price,
            String currency,
            String sector,
            String region
    ) {
        this.instrumentName = requireNonBlank(instrumentName, "instrumentName");
        this.ticker = blankToNull(ticker);
        this.isin = blankToNull(isin);

        this.quantity = requirePositive(quantity, "quantity");
        this.price = requireNonNegative(price, "price");
        this.currency = requireNonBlank(currency, "currency");

        this.sector = blankToNull(sector);
        this.region = blankToNull(region);
    }

    /**
     * Derived: market value = quantity * price
     */
    public BigDecimal getMarketValue() {
        return quantity.multiply(price).setScale(2, RoundingMode.HALF_UP);
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private static BigDecimal requirePositive(BigDecimal value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(field + " must be > 0");
        }
        return value;
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
        return value;
    }
}
