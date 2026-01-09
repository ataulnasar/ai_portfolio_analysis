package com.ai.stocks.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "commentary",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_commentary_snapshot_prompt", columnNames = {"snapshot_id", "prompt_version"})
        }
)
public class CommentaryEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private PortfolioSnapshotEntity snapshot;

    @Column(name = "model", nullable = false, length = 64)
    private String model;

    @Column(name = "prompt_version", nullable = false, length = 64)
    private String promptVersion;

    @Column(name = "sections_json", columnDefinition = "jsonb", nullable = false)
    private String sectionsJson; // JSON of CommentarySections

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PortfolioSnapshotEntity getSnapshot() { return snapshot; }
    public void setSnapshot(PortfolioSnapshotEntity snapshot) { this.snapshot = snapshot; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public String getSectionsJson() { return sectionsJson; }
    public void setSectionsJson(String sectionsJson) { this.sectionsJson = sectionsJson; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
