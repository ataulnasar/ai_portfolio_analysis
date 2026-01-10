package com.ai.stocks.service;

import com.ai.stocks.api.error.GlobalExceptionHandler;
import com.ai.stocks.domain.PortfolioSnapshot;
import com.ai.stocks.persistence.SnapshotEntityMapper;
import com.ai.stocks.persistence.repo.PortfolioSnapshotJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JpaSnapshotRepositoryAdapter implements SnapshotRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaSnapshotRepositoryAdapter.class);
    private final PortfolioSnapshotJpaRepository jpa;
    private final SnapshotEntityMapper mapper;

    public JpaSnapshotRepositoryAdapter(PortfolioSnapshotJpaRepository jpa, SnapshotEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
        log.info("Using JpaSnapshotRepositoryAdapter (Postgres-backed)");

    }

    @Override
    public PortfolioSnapshot save(PortfolioSnapshot snapshot) {
        var entity = mapper.toEntity(snapshot);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PortfolioSnapshot> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PortfolioSnapshot> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }
}
