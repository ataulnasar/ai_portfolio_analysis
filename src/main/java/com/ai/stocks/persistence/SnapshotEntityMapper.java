package com.ai.stocks.persistence;

import com.ai.stocks.domain.*;
import com.ai.stocks.persistence.entity.EquityHoldingEntity;
import com.ai.stocks.persistence.entity.PortfolioSnapshotEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class SnapshotEntityMapper {

    private final ObjectMapper om;

    public SnapshotEntityMapper(ObjectMapper om) {
        this.om = om;
    }

    public PortfolioSnapshotEntity toEntity(PortfolioSnapshot s) {
        PortfolioSnapshotEntity e = new PortfolioSnapshotEntity();
        e.setId(s.id());
        e.setAsOfDate(s.asOfDate());
        e.setBaseCurrency(s.baseCurrency());
        e.setSource(s.source().name());

        e.setTotalMarketValue(s.totalMarketValue());
        e.setNumberOfHoldings(s.stats().numberOfHoldings());
        e.setTopHoldingWeightPercent(s.stats().topHoldingWeightPercent());
        e.setTop3ConcentrationPercent(s.stats().top3ConcentrationPercent());

        e.setCreatedAt(OffsetDateTime.now());

        e.setTopHoldingsJson(writeJson(s.topHoldings()));
        e.setSectorExposureJson(writeJson(s.sectorExposure()));
        e.setRegionExposureJson(writeJson(s.regionExposure()));
        e.setCurrencyExposureJson(writeJson(s.currencyExposure()));

        List<EquityHoldingEntity> holdingEntities = s.holdings().stream()
                .map(h -> toHoldingEntity(h, e))
                .toList();
        e.setHoldings(holdingEntities);

        return e;
    }

    public PortfolioSnapshot toDomain(PortfolioSnapshotEntity e) {
        List<EquityHolding> holdings = e.getHoldings().stream()
                .map(this::toDomainHolding)
                .toList();

        List<TopHolding> topHoldings = readJsonList(e.getTopHoldingsJson(), TopHolding.class);

        SnapshotStats stats = new SnapshotStats(
                e.getNumberOfHoldings(),
                e.getTopHoldingWeightPercent(),
                e.getTop3ConcentrationPercent()
        );

        return new PortfolioSnapshot(
                e.getId(),
                e.getAsOfDate(),
                e.getBaseCurrency(),
                PortfolioSource.valueOf(e.getSource()),
                holdings,
                e.getTotalMarketValue(),
                topHoldings,
                readJsonMap(e.getSectorExposureJson()),
                readJsonMap(e.getRegionExposureJson()),
                readJsonMap(e.getCurrencyExposureJson()),
                stats
        );
    }

    private EquityHoldingEntity toHoldingEntity(EquityHolding equityHolding, PortfolioSnapshotEntity snapshot) {
        EquityHoldingEntity e = new EquityHoldingEntity();
        e.setSnapshot(snapshot);
        e.setInstrumentName(equityHolding.instrumentName());
        e.setTicker(equityHolding.ticker());
        e.setIsin(equityHolding.isin());
        e.setQuantity(equityHolding.quantity());
        e.setPrice(equityHolding.price());
        e.setCurrency(equityHolding.currency());
        e.setSector(equityHolding.sector());
        e.setRegion(equityHolding.region());
        e.setMarketValue(equityHolding.getMarketValue());
        return e;
    }

    private EquityHolding toDomainHolding(EquityHoldingEntity e) {
        // EquityHolding is record/class — adjust accessor style if needed
        return new EquityHolding(
                e.getInstrumentName(),
                e.getTicker(),
                e.getIsin(),
                e.getQuantity(),
                e.getPrice(),
                e.getCurrency(),
                e.getSector(),
                e.getRegion()
        );
    }

    private String writeJson(Object o) {
        try {
            return om.writeValueAsString(o);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize JSON for persistence", ex);
        }
    }

    private <T> List<T> readJsonList(String json, Class<T> elementType) {
        try {
            var type = om.getTypeFactory().constructCollectionType(List.class, elementType);
            return om.readValue(json, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize JSON list from persistence", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, java.math.BigDecimal> readJsonMap(String json) {
        try {
            var type = om.getTypeFactory().constructMapType(java.util.Map.class, String.class, java.math.BigDecimal.class);
            return om.readValue(json, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize JSON map from persistence", ex);
        }
    }
}
