package com.ai.stocks.persistence;

import com.ai.stocks.api.models.CommentaryResponse;
import com.ai.stocks.api.models.CommentarySections;
import com.ai.stocks.persistence.entity.CommentaryEntity;
import com.ai.stocks.persistence.entity.PortfolioSnapshotEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class CommentaryEntityMapper {

    private final ObjectMapper objectMapper;

    public CommentaryEntityMapper(ObjectMapper om) {
        this.objectMapper = om;
    }

    public CommentaryEntity toEntity(
            PortfolioSnapshotEntity snapshot,
            UUID id,
            String model,
            String promptVersion,
            CommentarySections sections
    ) {
        CommentaryEntity e = new CommentaryEntity();
        e.setId(id);
        e.setSnapshot(snapshot);
        e.setModel(model);
        e.setPromptVersion(promptVersion);
        e.setSectionsJson(writeJson(sections));
        e.setCreatedAt(OffsetDateTime.now());
        return e;
    }

    public CommentaryResponse toResponse(CommentaryEntity commentaryEntity) {
        CommentarySections sections = readSections(commentaryEntity.getSectionsJson());
        return new CommentaryResponse(
                commentaryEntity.getId(),
                commentaryEntity.getSnapshot().getId(),
                commentaryEntity.getCreatedAt(),
                sections,
                commentaryEntity.getPromptVersion(),
                commentaryEntity.getModel()
        );
    }

    private String writeJson(CommentarySections commentarySections) {
        try {
            return objectMapper.writeValueAsString(commentarySections);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize CommentarySections", ex);
        }
    }

    private CommentarySections readSections(String json) {
        try {
            return objectMapper.readValue(json, CommentarySections.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize CommentarySections", ex);
        }
    }
}
