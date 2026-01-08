package com.ai.stocks.api.models;

import java.util.List;

public record CsvValidationResponse(
        List<String> detectedColumns,
        int totalRows,
        List<CsvValidationMessage> messages,
        List<CsvPreviewRow> previewRows,
        boolean valid
) {}
