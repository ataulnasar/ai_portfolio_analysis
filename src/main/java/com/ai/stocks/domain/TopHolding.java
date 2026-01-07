package com.ai.stocks.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class TopHolding {
    private final String instrumentName;
    private final BigDecimal weightPercent;

    public TopHolding(String instrumentName, BigDecimal weightPercent) {
        if (instrumentName == null || instrumentName.trim().isEmpty()) {
            throw new IllegalArgumentException("instrumentName must not be blank");
        }
        this.instrumentName = instrumentName.trim();
        this.weightPercent = Objects.requireNonNull(weightPercent, "weightPercent");
    }

    public String getInstrumentName() { return instrumentName; }
    public BigDecimal getWeightPercent() { return weightPercent; }
}
