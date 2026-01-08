package com.ai.stocks.api.models;

import com.ai.stocks.domain.PortfolioSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SnapshotCreateRequest(
        @NotNull LocalDate asOfDate,
        @NotBlank String baseCurrency,
        @NotNull PortfolioSource source,
        @NotNull @Valid List<HoldingCreateRequest> holdings
) {}
