package com.ai.stocks.api.dto;

import java.math.BigDecimal;

public record SnapshotStatsResponse(
        int numberOfHoldings,
        BigDecimal topHoldingWeightPercent,
        BigDecimal top3ConcentrationPercent
) {}
