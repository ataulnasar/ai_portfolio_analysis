package com.ai.stocks.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "equity_holding")
public class EquityHoldingEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private PortfolioSnapshotEntity snapshot;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "isin")
    private String isin;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, precision = 19, scale = 6)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 8)
    private String currency;

    @Column(name = "sector")
    private String sector;

    @Column(name = "region")
    private String region;

    @Column(name = "market_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal marketValue;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PortfolioSnapshotEntity getSnapshot() { return snapshot; }
    public void setSnapshot(PortfolioSnapshotEntity snapshot) { this.snapshot = snapshot; }

    public String getInstrumentName() { return instrumentName; }
    public void setInstrumentName(String instrumentName) { this.instrumentName = instrumentName; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public BigDecimal getMarketValue() { return marketValue; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }
}
