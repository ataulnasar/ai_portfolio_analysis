package com.ai.stocks.service.llm;

import java.util.List;
import java.util.Map;

public final class JsonPathLite {
    private JsonPathLite() {}

    @SuppressWarnings("unchecked")
    public static String extractFirstOutputText(Map<String, Object> resp) {
        // Responses API: output -> [ { content: [ { type: "output_text", text: "..." } ] } ]
        Object outputObj = resp.get("output");
        if (!(outputObj instanceof List<?> outputList) || outputList.isEmpty()) {
            throw new IllegalStateException("LLM response missing 'output'");
        }
        Object first = outputList.get(0);
        if (!(first instanceof Map<?,?> firstMap)) {
            throw new IllegalStateException("LLM response malformed");
        }
        Object contentObj = ((Map<String,Object>) firstMap).get("content");
        if (!(contentObj instanceof List<?> contentList) || contentList.isEmpty()) {
            throw new IllegalStateException("LLM response missing 'content'");
        }
        Object c0 = contentList.get(0);
        if (!(c0 instanceof Map<?,?> c0m)) {
            throw new IllegalStateException("LLM content malformed");
        }
        Object text = ((Map<String,Object>) c0m).get("text");
        if (text == null) throw new IllegalStateException("LLM output missing text");
        return text.toString();
    }
}
