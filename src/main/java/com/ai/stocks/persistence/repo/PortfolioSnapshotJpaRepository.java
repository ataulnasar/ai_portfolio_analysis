package com.ai.stocks.persistence.repo;

import com.ai.stocks.persistence.entity.PortfolioSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortfolioSnapshotJpaRepository extends JpaRepository<PortfolioSnapshotEntity, UUID> {
}
