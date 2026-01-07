package com.ai.stocks.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record HoldingCreateRequest(
        @NotBlank String instrumentName,
        String ticker,
        String isin,
        @NotNull @Positive BigDecimal quantity,
        @NotNull BigDecimal price,
        @NotBlank String currency,
        String sector,
        String region
) {}
