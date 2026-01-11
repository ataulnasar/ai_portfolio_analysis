package com.ai.stocks.service.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OpenAiLlmClient implements LlmClient {

    private final RestClient restClient;
    private final ObjectMapper om;
    private final String model;

    private static final String PROMPT_VERSION = "commentary_v1";

    public OpenAiLlmClient(
            ObjectMapper om,
            @Value("${openai.apiKey}") String apiKey,
            @Value("${openai.baseUrl}") String baseUrl,
            @Value("${openai.model}") String model
    ) {
        this.om = om;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public LlmResult generateCommentary(CommentaryInput input) {
        try {
            String instructions = PromptTemplates.instructions();
            String userInput = PromptTemplates.userInputJson(om, input);

            // JSON schema that matches CommentarySections
            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "additionalProperties", false,
                    "required", java.util.List.of(
                            "summary",
                            "concentrationAndStructure",
                            "sectorExposure",
                            "geoAndCurrency",
                            "contextualNote",
                            "disclaimer"
                    ),
                    "properties", Map.of(
                            "summary", Map.of("type", "string"),
                            "concentrationAndStructure", Map.of("type", "string"),
                            "sectorExposure", Map.of("type", "string"),
                            "geoAndCurrency", Map.of("type", "string"),
                            "contextualNote", Map.of("type", "string"),
                            "disclaimer", Map.of("type", "string")
                    )
            );

            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("model", model);
            payload.put("instructions", instructions);
            payload.put("input", userInput);

            // ✅ New location for structured output settings in Responses API
            payload.put("text", Map.of(
                    "format", Map.of(
                            "type", "json_schema",
                            "name", "commentary_sections",
                            "schema", schema,
                            "strict", true
                    )
            ));

            // Deterministic output helps a lot
            payload.put("temperature", 0);
            payload.put("max_output_tokens", 700);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restClient.post()
                    .uri("/responses")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            String text = JsonPathLite.extractFirstOutputText(resp);
            return new LlmResult(model, PROMPT_VERSION, text);

        } catch (Exception e) {
            throw new IllegalStateException("LLM call failed: " + e.getMessage(), e);
        }
    }
}
