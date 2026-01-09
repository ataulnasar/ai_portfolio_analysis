package com.ai.stocks.service.llm;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class PromptTemplates {
    private PromptTemplates() {}

    public static String instructions() {
        return """
You are a financial reporting assistant.
Explain the structure and exposure of an equity portfolio based strictly on provided data.

Rules:
- Neutral, factual language only
- No recommendations, no predictions, no advice
- Do not evaluate past decisions
- Do not mention performance/returns
- If data is missing, state it explicitly
- Output MUST be valid JSON matching the required schema

Required JSON schema:
{
  "summary": "string",
  "concentrationAndStructure": "string",
  "sectorExposure": "string",
  "geoAndCurrency": "string",
  "contextualNote": "string",
  "disclaimer": "string (must be present, last line style)"
}

Disclaimer must be:
"This commentary is for informational purposes only and does not constitute investment advice or a recommendation to buy or sell any securities."
""";
    }

    public static String userInputJson(ObjectMapper om, CommentaryInput input) {
        try {
            String facts = om.writerWithDefaultPrettyPrinter().writeValueAsString(input);
            return """
Generate commentary in the required JSON schema using only these facts:

FACTS_JSON:
""" + facts + "\n";
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize CommentaryInput", e);
        }
    }
}
