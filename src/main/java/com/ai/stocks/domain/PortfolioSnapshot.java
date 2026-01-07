package com.ai.stocks.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PortfolioSnapshot {

    private final UUID id;
    private final LocalDate asOfDate;
    private final String baseCurrency;
    private final PortfolioSource source;

    private final List<EquityHolding> holdings;

    // Derived fields (stored for reproducibility)
    private final BigDecimal totalMarketValue;
    private final List<TopHolding> topHoldings; // name + weight
    private final Map<String, BigDecimal> sectorExposure;   // sector -> %
    private final Map<String, BigDecimal> regionExposure;   // region -> %
    private final Map<String, BigDecimal> currencyExposure; // currency -> %
    private final SnapshotStats stats;

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

    public UUID getId() { return id; }
    public LocalDate getAsOfDate() { return asOfDate; }
    public String getBaseCurrency() { return baseCurrency; }
    public PortfolioSource getSource() { return source; }
    public List<EquityHolding> getHoldings() { return holdings; }

    public BigDecimal getTotalMarketValue() { return totalMarketValue; }
    public List<TopHolding> getTopHoldings() { return topHoldings; }
    public Map<String, BigDecimal> getSectorExposure() { return sectorExposure; }
    public Map<String, BigDecimal> getRegionExposure() { return regionExposure; }
    public Map<String, BigDecimal> getCurrencyExposure() { return currencyExposure; }
    public SnapshotStats getStats() { return stats; }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
