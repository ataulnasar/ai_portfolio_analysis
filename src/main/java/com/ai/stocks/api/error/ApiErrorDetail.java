package com.ai.stocks.api.error;

public record ApiErrorDetail(
        String field,
        String issue
) {}
