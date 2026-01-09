package com.ai.stocks.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio_snapshot")
public class PortfolioSnapshotEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    @Column(name = "base_currency", nullable = false, length = 8)
    private String baseCurrency;

    @Column(name = "source", nullable = false, length = 32)
    private String source; // store enum as String (CSV/DEMO/AVANZA_EXPERIMENTAL)

    // Derived / stored fields
    @Column(name = "total_market_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalMarketValue;

    @Column(name = "number_of_holdings", nullable = false)
    private int numberOfHoldings;

    @Column(name = "top_holding_weight_pct", nullable = false, precision = 7, scale = 2)
    private BigDecimal topHoldingWeightPercent;

    @Column(name = "top3_concentration_pct", nullable = false, precision = 7, scale = 2)
    private BigDecimal top3ConcentrationPercent;

    // JSONB columns for reproducibility / easy evolution
    @Column(name = "top_holdings_json", columnDefinition = "jsonb", nullable = false)
    private String topHoldingsJson; // list of {instrumentName, weightPercent}

    @Column(name = "sector_exposure_json", columnDefinition = "jsonb", nullable = false)
    private String sectorExposureJson; // map {sector: percent}

    @Column(name = "region_exposure_json", columnDefinition = "jsonb", nullable = false)
    private String regionExposureJson;

    @Column(name = "currency_exposure_json", columnDefinition = "jsonb", nullable = false)
    private String currencyExposureJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquityHoldingEntity> holdings = new ArrayList<>();

    // --- getters/setters (generate in IDE) ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public BigDecimal getTotalMarketValue() { return totalMarketValue; }
    public void setTotalMarketValue(BigDecimal totalMarketValue) { this.totalMarketValue = totalMarketValue; }

    public int getNumberOfHoldings() { return numberOfHoldings; }
    public void setNumberOfHoldings(int numberOfHoldings) { this.numberOfHoldings = numberOfHoldings; }

    public BigDecimal getTopHoldingWeightPercent() { return topHoldingWeightPercent; }
    public void setTopHoldingWeightPercent(BigDecimal topHoldingWeightPercent) { this.topHoldingWeightPercent = topHoldingWeightPercent; }

    public BigDecimal getTop3ConcentrationPercent() { return top3ConcentrationPercent; }
    public void setTop3ConcentrationPercent(BigDecimal top3ConcentrationPercent) { this.top3ConcentrationPercent = top3ConcentrationPercent; }

    public String getTopHoldingsJson() { return topHoldingsJson; }
    public void setTopHoldingsJson(String topHoldingsJson) { this.topHoldingsJson = topHoldingsJson; }

    public String getSectorExposureJson() { return sectorExposureJson; }
    public void setSectorExposureJson(String sectorExposureJson) { this.sectorExposureJson = sectorExposureJson; }

    public String getRegionExposureJson() { return regionExposureJson; }
    public void setRegionExposureJson(String regionExposureJson) { this.regionExposureJson = regionExposureJson; }

    public String getCurrencyExposureJson() { return currencyExposureJson; }
    public void setCurrencyExposureJson(String currencyExposureJson) { this.currencyExposureJson = currencyExposureJson; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<EquityHoldingEntity> getHoldings() { return holdings; }
    public void setHoldings(List<EquityHoldingEntity> holdings) { this.holdings = holdings; }
}
