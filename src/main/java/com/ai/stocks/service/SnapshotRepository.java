package com.ai.stocks.service;

import com.ai.stocks.domain.PortfolioSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnapshotRepository {
    PortfolioSnapshot save(PortfolioSnapshot snapshot);
    Optional<PortfolioSnapshot> findById(UUID id);
    List<PortfolioSnapshot> findAll();
}
