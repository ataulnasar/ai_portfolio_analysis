package com.ai.stocks.api.dto;

import java.math.BigDecimal;

public record TopHoldingResponse(
        String instrumentName,
        BigDecimal weightPercent
) {}
