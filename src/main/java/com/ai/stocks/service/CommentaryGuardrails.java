package com.ai.stocks.service;

import com.ai.stocks.api.models.CommentarySections;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class CommentaryGuardrails {

    private static final List<String> BANNED = List.of(
            "should", "buy", "sell", "recommend", "forecast", "predict",
            "undervalued", "overvalued", "outperform", "underperform", "opportunity"
    );

    private static final String REQUIRED_DISCLAIMER =
            "This commentary is for informational purposes only and does not constitute investment advice or a recommendation to buy or sell any securities.";

    private final ObjectMapper om;

    public CommentaryGuardrails(ObjectMapper om) {
        this.om = om;
    }

    public CommentarySections parseAndValidate(String rawJson) {
        try {
            CommentarySections sections = om.readValue(rawJson, CommentarySections.class);

            // Disclaimer must match exactly (keeps compliance consistent)
            if (sections.disclaimer() == null || !sections.disclaimer().trim().equals(REQUIRED_DISCLAIMER)) {
                throw new IllegalArgumentException("Disclaimer missing or not in required form.");
            }

            // banned words check across all text
            String all = String.join(" ",
                    sections.summary(),
                    sections.concentrationAndStructure(),
                    sections.sectorExposure(),
                    sections.geoAndCurrency(),
                    sections.contextualNote()
            ).toLowerCase();

            for (String b : BANNED) {
                Pattern p = Pattern.compile("\\b" + Pattern.quote(b) + "\\b", Pattern.CASE_INSENSITIVE);
                if (p.matcher(all).find()) {
                    throw new IllegalArgumentException("Generated text contains banned term: " + b);
                }
            }

            return sections;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("LLM output is not valid JSON for CommentarySections.", e);
        }
    }
}
