package com.ai.stocks.api.models;

public record CommentarySections(
        String summary,
        String concentrationAndStructure,
        String sectorExposure,
        String geoAndCurrency,
        String contextualNote,
        String disclaimer
) {}
