package com.ai.stocks.service;

import com.ai.stocks.api.models.CommentaryResponse;
import com.ai.stocks.api.models.CommentarySections;
import com.ai.stocks.service.llm.CommentaryInput;
import com.ai.stocks.service.llm.LlmClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import com.ai.stocks.persistence.CommentaryEntityMapper;
import com.ai.stocks.persistence.repo.CommentaryJpaRepository;
import com.ai.stocks.persistence.repo.PortfolioSnapshotJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentaryService {

    private final SnapshotService snapshotService;
    private final LlmClient llmClient;
    private final CommentaryGuardrails guardrails;
    private static final String PROMPT_VERSION = "commentary_v1";
    private final PortfolioSnapshotJpaRepository snapshotJpa;
    private final CommentaryJpaRepository commentaryJpa;
    private final CommentaryEntityMapper mapper;

    public CommentaryService(
            SnapshotService snapshotService,
            PortfolioSnapshotJpaRepository snapshotJpa,
            CommentaryJpaRepository commentaryJpa,
            LlmClient llmClient,
            CommentaryGuardrails guardrails,
            CommentaryEntityMapper mapper
    ) {
        this.snapshotService = snapshotService;
        this.snapshotJpa = snapshotJpa;
        this.commentaryJpa = commentaryJpa;
        this.llmClient = llmClient;
        this.guardrails = guardrails;
        this.mapper = mapper;
    }

    @Transactional
    public CommentaryResponse generate(UUID snapshotId) {
        return commentaryJpa.findBySnapshot_IdAndPromptVersion(snapshotId, PROMPT_VERSION)
                .map(mapper::toResponse)
                .orElseGet(() -> generateAndPersist(snapshotId));
    }

    private CommentaryResponse generateAndPersist(UUID snapshotId) {
        // 2) Load snapshot entity for FK
        var snapshotEntity = snapshotJpa.findById(snapshotId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Snapshot not found: " + snapshotId));

        // 3) Build safe input (domain)
        var s = snapshotService.getSnapshot(snapshotId);
        CommentaryInput input = new CommentaryInput(
                s.asOfDate(),
                s.baseCurrency(),
                s.totalMarketValue(),
                s.stats().numberOfHoldings(),
                s.stats().topHoldingWeightPercent(),
                s.stats().top3ConcentrationPercent(),
                s.topHoldings(),
                s.sectorExposure(),
                s.regionExposure(),
                s.currencyExposure()
        );

        // 4) Call LLM
        var llm = llmClient.generateCommentary(input);

        // 5) Parse + guardrails
        CommentarySections sections = guardrails.parseAndValidate(llm.rawJson());

        // 6) Persist
        var entity = mapper.toEntity(
                snapshotEntity,
                UUID.randomUUID(),
                llm.model(),
                PROMPT_VERSION,
                sections
        );

        var saved = commentaryJpa.save(entity);
        return mapper.toResponse(saved);
    }

    public CommentaryInput buildInput(UUID snapshotId) {
        var s = snapshotService.getSnapshot(snapshotId);

        return new CommentaryInput(
                s.asOfDate(),
                s.baseCurrency(),
                s.totalMarketValue(),
                s.stats().numberOfHoldings(),
                s.stats().topHoldingWeightPercent(),
                s.stats().top3ConcentrationPercent(),
                s.topHoldings(),
                s.sectorExposure(),
                s.regionExposure(),
                s.currencyExposure()
        );
    }

    @Transactional(readOnly = true)
    public CommentaryResponse getForSnapshot(UUID snapshotId, Optional<String> promptVersionOpt) {

        if (promptVersionOpt.isPresent() && !promptVersionOpt.get().isBlank()) {
            String pv = promptVersionOpt.get().trim();
            return commentaryJpa.findBySnapshot_IdAndPromptVersion(snapshotId, pv)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new java.util.NoSuchElementException(
                            "Commentary not found for snapshot " + snapshotId + " and promptVersion " + pv
                    ));
        }

        // default: latest commentary for snapshot
        return commentaryJpa.findAllBySnapshot_IdOrderByCreatedAtDesc(snapshotId).stream()
                .findFirst()
                .map(mapper::toResponse)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Commentary not found for snapshot " + snapshotId
                ));
    }
}
