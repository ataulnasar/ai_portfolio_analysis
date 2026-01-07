package com.ai.stocks.api.dto;

import com.ai.stocks.domain.PortfolioSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SnapshotResponse(
        UUID id,
        LocalDate asOfDate,
        String baseCurrency,
        PortfolioSource source,

        BigDecimal totalMarketValue,
        SnapshotStatsResponse stats,

        List<TopHoldingResponse> topHoldings,
        Map<String, BigDecimal> sectorExposure,
        Map<String, BigDecimal> regionExposure,
        Map<String, BigDecimal> currencyExposure,

        List<HoldingResponse> holdings
) {}
