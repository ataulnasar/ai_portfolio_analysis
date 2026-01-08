package com.ai.stocks.api.models;

import java.math.BigDecimal;

public record SnapshotStatsResponse(
        int numberOfHoldings,
        BigDecimal topHoldingWeightPercent,
        BigDecimal top3ConcentrationPercent
) {}
