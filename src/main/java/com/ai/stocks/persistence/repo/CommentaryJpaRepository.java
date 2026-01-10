package com.ai.stocks.persistence.repo;

import com.ai.stocks.persistence.entity.CommentaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentaryJpaRepository extends JpaRepository<CommentaryEntity, UUID> {
    Optional<CommentaryEntity> findBySnapshot_IdAndPromptVersion(UUID snapshotId, String promptVersion);
}
