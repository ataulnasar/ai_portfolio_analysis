package com.ai.stocks.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record SnapshotStats(int numberOfHoldings, BigDecimal topHoldingWeightPercent,
                            BigDecimal top3ConcentrationPercent) {
    public SnapshotStats(int numberOfHoldings, BigDecimal topHoldingWeightPercent, BigDecimal top3ConcentrationPercent) {
        if (numberOfHoldings < 0) throw new IllegalArgumentException("numberOfHoldings must be >= 0");
        this.numberOfHoldings = numberOfHoldings;
        this.topHoldingWeightPercent = Objects.requireNonNull(topHoldingWeightPercent, "topHoldingWeightPercent");
        this.top3ConcentrationPercent = Objects.requireNonNull(top3ConcentrationPercent, "top3ConcentrationPercent");
    }
}
