package com.ai.stocks.api;

import com.ai.stocks.api.models.CsvValidationResponse;
import com.ai.stocks.api.models.SnapshotResponse;
import com.ai.stocks.domain.PortfolioSource;
import com.ai.stocks.service.CsvPortfolioParser;
import com.ai.stocks.service.SnapshotService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ImportController {

    private final CsvPortfolioParser parser;
    private final SnapshotService snapshotService;
    private final SnapshotController snapshotControllerMapper; // reuse mapping method? (simple approach below)

    public ImportController(CsvPortfolioParser parser, SnapshotService snapshotService) {
        this.parser = parser;
        this.snapshotService = snapshotService;
        this.snapshotControllerMapper = null;
    }

    @PostMapping(value = "/csv/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvValidationResponse> validateCsv(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "20") int previewLimit
    ) throws Exception {
        byte[] bytes = file.getBytes();
        CsvValidationResponse resp = parser.validate(bytes, previewLimit);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SnapshotResponse> importCsv(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "SEK") String baseCurrency,
            @RequestParam(defaultValue = "CSV") PortfolioSource source
    ) throws Exception {
        byte[] bytes = file.getBytes();

        // Parse into the existing SnapshotCreateRequest DTO and reuse SnapshotService
        var createReq = parser.parseToSnapshotCreateRequest(bytes, source, baseCurrency);
        var snapshot = snapshotService.createSnapshot(createReq);

        // Map to SnapshotResponse (reuse logic by copying mapping code here)
        SnapshotResponse response = SnapshotResponseMapper.toResponse(snapshot);
        return ResponseEntity.ok(response);
    }
}
