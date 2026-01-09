package com.ai.stocks.service.llm;

public record LlmResult(
        String model,
        String promptVersion,
        String rawJson
) {}
