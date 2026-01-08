package com.ai.stocks.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @param totalMarketValue Derived fields (stored for reproducibility)
 * @param topHoldings      name + weight
 * @param sectorExposure   sector -> %
 * @param regionExposure   region -> %
 * @param currencyExposure currency -> %
 */
public record PortfolioSnapshot(UUID id, LocalDate asOfDate, String baseCurrency, PortfolioSource source,
                                List<EquityHolding> holdings, BigDecimal totalMarketValue, List<TopHolding> topHoldings,
                                Map<String, BigDecimal> sectorExposure, Map<String, BigDecimal> regionExposure,
                                Map<String, BigDecimal> currencyExposure, SnapshotStats stats) {

    public PortfolioSnapshot(
            UUID id,
            LocalDate asOfDate,
            String baseCurrency,
            PortfolioSource source,
            List<EquityHolding> holdings,
            BigDecimal totalMarketValue,
            List<TopHolding> topHoldings,
            Map<String, BigDecimal> sectorExposure,
            Map<String, BigDecimal> regionExposure,
            Map<String, BigDecimal> currencyExposure,
            SnapshotStats stats
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.asOfDate = Objects.requireNonNull(asOfDate, "asOfDate");
        this.baseCurrency = requireNonBlank(baseCurrency, "baseCurrency");
        this.source = Objects.requireNonNull(source, "source");

        this.holdings = List.copyOf(Objects.requireNonNull(holdings, "holdings"));

        this.totalMarketValue = Objects.requireNonNull(totalMarketValue, "totalMarketValue");
        this.topHoldings = List.copyOf(Objects.requireNonNull(topHoldings, "topHoldings"));
        this.sectorExposure = Map.copyOf(Objects.requireNonNull(sectorExposure, "sectorExposure"));
        this.regionExposure = Map.copyOf(Objects.requireNonNull(regionExposure, "regionExposure"));
        this.currencyExposure = Map.copyOf(Objects.requireNonNull(currencyExposure, "currencyExposure"));
        this.stats = Objects.requireNonNull(stats, "stats");
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
