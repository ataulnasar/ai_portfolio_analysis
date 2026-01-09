package com.ai.stocks.api.models;

import com.ai.stocks.domain.TopHolding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CommentaryInputResponse(
        UUID snapshotId,
        LocalDate asOfDate,
        String baseCurrency,
        BigDecimal totalMarketValue,
        int numberOfHoldings,
        BigDecimal topHoldingWeightPercent,
        BigDecimal top3ConcentrationPercent,
        List<TopHolding> topHoldings,
        Map<String, BigDecimal> sectorExposure,
        Map<String, BigDecimal> regionExposure,
        Map<String, BigDecimal> currencyExposure
) {}
