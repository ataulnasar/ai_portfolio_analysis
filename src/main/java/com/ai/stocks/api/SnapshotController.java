package com.ai.stocks.api;

import com.ai.stocks.api.models.*;
import com.ai.stocks.domain.PortfolioSnapshot;
import com.ai.stocks.service.SnapshotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/snapshots")
public class SnapshotController {

    private final SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @PostMapping
    public ResponseEntity<SnapshotResponse> create(@Valid @RequestBody SnapshotCreateRequest request) {
        PortfolioSnapshot created = snapshotService.createSnapshot(request);
        return ResponseEntity.ok(toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SnapshotResponse> getById(@PathVariable UUID id) {
        PortfolioSnapshot snapshot = snapshotService.getSnapshot(id);
        return ResponseEntity.ok(toResponse(snapshot));
    }

    @GetMapping
    public ResponseEntity<List<SnapshotResponse>> list() {
        List<SnapshotResponse> items = snapshotService.listSnapshots().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    private SnapshotResponse toResponse(PortfolioSnapshot portfolioSnapshot) {
        List<HoldingResponse> holdingResponseList = portfolioSnapshot.holdings().stream()
                .map(equityHolding -> new HoldingResponse(
                        equityHolding.instrumentName(),
                        equityHolding.ticker(),
                        equityHolding.isin(),
                        equityHolding.quantity(),
                        equityHolding.price(),
                        equityHolding.currency(),
                        equityHolding.sector(),
                        equityHolding.region(),
                        equityHolding.getMarketValue()
                ))
                .toList();

        List<TopHoldingResponse> topHoldingResponses = portfolioSnapshot.topHoldings().stream()
                .map(t -> new TopHoldingResponse(t.instrumentName(), t.weightPercent()))
                .toList();

        SnapshotStatsResponse snapshotStatsResponse = new SnapshotStatsResponse(
                portfolioSnapshot.stats().numberOfHoldings(),
                portfolioSnapshot.stats().topHoldingWeightPercent(),
                portfolioSnapshot.stats().top3ConcentrationPercent()
        );

        return new SnapshotResponse(
                portfolioSnapshot.id(),
                portfolioSnapshot.asOfDate(),
                portfolioSnapshot.baseCurrency(),
                portfolioSnapshot.source(),
                portfolioSnapshot.totalMarketValue(),
                snapshotStatsResponse,
                topHoldingResponses,
                portfolioSnapshot.sectorExposure(),
                portfolioSnapshot.regionExposure(),
                portfolioSnapshot.currencyExposure(),
                holdingResponseList
        );
    }
}
