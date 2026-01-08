package com.ai.stocks.api.models;

import java.math.BigDecimal;

public record HoldingResponse(
        String instrumentName,
        String ticker,
        String isin,
        BigDecimal quantity,
        BigDecimal price,
        String currency,
        String sector,
        String region,
        BigDecimal marketValue
) {}
