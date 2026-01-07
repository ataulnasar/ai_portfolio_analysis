package com.ai.stocks.api;

import com.ai.stocks.api.dto.*;
import com.ai.stocks.domain.PortfolioSnapshot;
import com.ai.stocks.service.SnapshotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
        List<HoldingResponse> holdings = s.getHoldings().stream()
                .map(h -> new HoldingResponse(
                        h.getInstrumentName(),
                        h.getTicker(),
                        h.getIsin(),
                        h.getQuantity(),
                        h.getPrice(),
                        h.getCurrency(),
                        h.getSector(),
                        h.getRegion(),
                        h.getMarketValue()
                ))
                .toList();

        List<TopHoldingResponse> topHoldings = s.getTopHoldings().stream()
                .map(t -> new TopHoldingResponse(t.getInstrumentName(), t.getWeightPercent()))
                .toList();

        SnapshotStatsResponse stats = new SnapshotStatsResponse(
                s.getStats().getNumberOfHoldings(),
                s.getStats().getTopHoldingWeightPercent(),
                s.getStats().getTop3ConcentrationPercent()
        );

        return new SnapshotResponse(
                s.getId(),
                s.getAsOfDate(),
                s.getBaseCurrency(),
                s.getSource(),
                s.getTotalMarketValue(),
                stats,
                topHoldings,
                s.getSectorExposure(),
                s.getRegionExposure(),
                s.getCurrencyExposure(),
                holdings
        );
    }
}
