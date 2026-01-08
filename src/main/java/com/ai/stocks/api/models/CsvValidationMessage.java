package com.ai.stocks.api.models;

public record CsvValidationMessage(
        String severity,   // "ERROR" or "WARNING"
        String code,       // e.g. "MISSING_REQUIRED_COLUMN"
        String message,
        Integer rowNumber  // null if not row-specific
) {}
