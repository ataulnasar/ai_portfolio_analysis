package com.ai.stocks.service.llm;

public interface LlmClient {
    LlmResult generateCommentary(CommentaryInput input);
}
