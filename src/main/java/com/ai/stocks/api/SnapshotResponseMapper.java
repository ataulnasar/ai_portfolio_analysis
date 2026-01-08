package com.ai.stocks.api;

import com.ai.stocks.api.models.*;
import com.ai.stocks.domain.PortfolioSnapshot;

import java.util.List;

public final class SnapshotResponseMapper {
    private SnapshotResponseMapper() {}

    public static SnapshotResponse toResponse(PortfolioSnapshot s) {
        List<HoldingResponse> holdings = s.holdings().stream()
                .map(h -> new HoldingResponse(
                        h.instrumentName(),
                        h.ticker(),
                        h.isin(),
                        h.quantity(),
                        h.price(),
                        h.currency(),
                        h.sector(),
                        h.region(),
                        h.getMarketValue()
                ))
                .toList();

        List<TopHoldingResponse> topHoldings = s.topHoldings().stream()
                .map(topHolding -> new TopHoldingResponse(topHolding.instrumentName(), topHolding.weightPercent()))
                .toList();

        SnapshotStatsResponse stats = new SnapshotStatsResponse(
                s.stats().numberOfHoldings(),
                s.stats().topHoldingWeightPercent(),
                s.stats().top3ConcentrationPercent()
        );

        return new SnapshotResponse(
                s.id(),
                s.asOfDate(),
                s.baseCurrency(),
                s.source(),
                s.totalMarketValue(),
                stats,
                topHoldings,
                s.sectorExposure(),
                s.regionExposure(),
                s.currencyExposure(),
                holdings
        );
    }
}
