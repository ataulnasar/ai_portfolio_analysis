package com.ai.stocks.api.models;

import java.util.Map;

public record CsvPreviewRow(
        int rowNumber,
        Map<String, String> values
) {}
