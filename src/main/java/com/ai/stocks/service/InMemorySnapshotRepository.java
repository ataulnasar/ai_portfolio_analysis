package com.ai.stocks.service;

import com.ai.stocks.domain.PortfolioSnapshot;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySnapshotRepository implements SnapshotRepository {

    private final Map<UUID, PortfolioSnapshot> store = new ConcurrentHashMap<>();

    @Override
    public PortfolioSnapshot save(PortfolioSnapshot snapshot) {
        store.put(snapshot.getId(), snapshot);
        return snapshot;
    }

    @Override
    public Optional<PortfolioSnapshot> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<PortfolioSnapshot> findAll() {
        // Return newest first is nice, but we don't have createdAt in domain yet.
        return new ArrayList<>(store.values());
    }
}
