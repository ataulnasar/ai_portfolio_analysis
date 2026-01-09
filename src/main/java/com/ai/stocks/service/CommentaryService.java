package com.ai.stocks.service;

import com.ai.stocks.api.models.CommentaryResponse;
import com.ai.stocks.api.models.CommentarySections;
import com.ai.stocks.domain.PortfolioSnapshot;
import com.ai.stocks.service.llm.CommentaryInput;
import com.ai.stocks.service.llm.LlmClient;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CommentaryService {

    private final SnapshotService snapshotService;
    private final LlmClient llmClient;
    private final CommentaryGuardrails guardrails;

    public CommentaryService(SnapshotService snapshotService, LlmClient llmClient, CommentaryGuardrails guardrails) {
        this.snapshotService = snapshotService;
        this.llmClient = llmClient;
        this.guardrails = guardrails;
    }

    public CommentaryResponse generate(UUID snapshotId) {
        PortfolioSnapshot s = snapshotService.getSnapshot(snapshotId);

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

        var llm = llmClient.generateCommentary(input);
        CommentarySections sections = guardrails.parseAndValidate(llm.rawJson());

        return new CommentaryResponse(
                UUID.randomUUID(),
                snapshotId,
                OffsetDateTime.now(),
                sections,
                llm.promptVersion(),
                llm.model()
        );
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
}
