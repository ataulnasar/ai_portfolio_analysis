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

            Map<String, Object> payload = Map.of(
                    "model", model,
                    "instructions", instructions,
                    "input", userInput
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restClient.post()
                    .uri("/responses")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            // Extract output_text from Responses API
            // We keep it simple: most responses include output[0].content[0].text
            String text = JsonPathLite.extractFirstOutputText(resp);

            return new LlmResult(model, PROMPT_VERSION, text);
        } catch (Exception e) {
            throw new IllegalStateException("LLM call failed: " + e.getMessage(), e);
        }
    }
}
