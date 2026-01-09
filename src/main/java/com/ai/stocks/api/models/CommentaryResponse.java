package com.ai.stocks.api.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentaryResponse(
        UUID id,
        UUID snapshotId,
        OffsetDateTime createdAt,
        CommentarySections sections,
        String promptVersion,
        String model
) {}
