package com.ai.stocks.api.models;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CommentaryCreateRequest(
        @NotNull UUID snapshotId
) {}
