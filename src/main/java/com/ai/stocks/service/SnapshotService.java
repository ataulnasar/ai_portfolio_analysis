package com.ai.stocks.service;

import com.ai.stocks.api.dto.HoldingCreateRequest;
import com.ai.stocks.api.dto.SnapshotCreateRequest;
import com.ai.stocks.domain.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SnapshotService {

    private final SnapshotCalculator calculator;
    private final SnapshotRepository repository;

    public SnapshotService(SnapshotCalculator calculator, SnapshotRepository repository) {
        this.calculator = calculator;
        this.repository = repository;
    }

    public PortfolioSnapshot createSnapshot(SnapshotCreateRequest request) {
        List<EquityHolding> holdings = request.holdings().stream()
                .map(this::toDomainHolding)
                .toList();

        SnapshotCalculator.CalculatedSnapshot calculated = calculator.calculate(holdings);

        PortfolioSnapshot snapshot = new PortfolioSnapshot(
                UUID.randomUUID(),
                request.asOfDate(),
                request.baseCurrency(),
                request.source(),
                holdings,
                calculated.totalMarketValue(),
                calculated.topHoldings(),
                calculated.sectorExposure(),
                calculated.regionExposure(),
                calculated.currencyExposure(),
                calculated.stats()
        );

        return repository.save(snapshot);
    }

    public PortfolioSnapshot getSnapshot(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Snapshot not found: " + id));
    }

    public List<PortfolioSnapshot> listSnapshots() {
        return repository.findAll();
    }

    private EquityHolding toDomainHolding(HoldingCreateRequest h) {
        return new EquityHolding(
                h.instrumentName(),
                h.ticker(),
                h.isin(),
                h.quantity(),
                h.price(),
                h.currency(),
                h.sector(),
                h.region()
        );
    }
}
