package com.ai.stocks.api.models;

import java.math.BigDecimal;

public record TopHoldingResponse(
        String instrumentName,
        BigDecimal weightPercent
) {}
