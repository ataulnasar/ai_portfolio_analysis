package com.ai.stocks.api;

import com.ai.stocks.api.models.CommentaryCreateRequest;
import com.ai.stocks.api.models.CommentaryInputResponse;
import com.ai.stocks.api.models.CommentaryResponse;
import com.ai.stocks.service.CommentaryService;
import com.ai.stocks.service.llm.CommentaryInput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/commentary")
public class CommentaryController {

    private final CommentaryService service;

    public CommentaryController(CommentaryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CommentaryResponse> create(@Valid @RequestBody CommentaryCreateRequest req) {
        return ResponseEntity.ok(service.generate(req.snapshotId()));
    }

    @GetMapping("/input/{snapshotId}")
    public ResponseEntity<CommentaryInputResponse> previewInput(@PathVariable UUID snapshotId) {
        CommentaryInput input = service.buildInput(snapshotId);

        return ResponseEntity.ok(new CommentaryInputResponse(
                snapshotId,
                input.asOfDate(),
                input.baseCurrency(),
                input.totalMarketValue(),
                input.numberOfHoldings(),
                input.topHoldingWeightPercent(),
                input.top3ConcentrationPercent(),
                input.topHoldings(),
                input.sectorExposure(),
                input.regionExposure(),
                input.currencyExposure()
        ));
    }
}
