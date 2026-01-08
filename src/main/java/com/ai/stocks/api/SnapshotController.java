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

    private SnapshotResponse toResponse(PortfolioSnapshot s) {
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
                .map(t -> new TopHoldingResponse(t.instrumentName(), t.weightPercent()))
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
