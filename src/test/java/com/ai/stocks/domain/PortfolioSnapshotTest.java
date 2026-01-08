package com.ai.stocks.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioSnapshotTest {

    @Test
    void canCreateSnapshotObject() {
        EquityHolding h1 = new EquityHolding(
                "Qlucore", null, null,
                new BigDecimal("21100"),
                new BigDecimal("1.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        EquityHolding h2 = new EquityHolding(
                "Sinch", null, null,
                new BigDecimal("120"),
                new BigDecimal("29.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        SnapshotStats stats = new SnapshotStats(
                2,
                new BigDecimal("85.00"),
                new BigDecimal("100.00")
        );

        PortfolioSnapshot snapshot = new PortfolioSnapshot(
                UUID.randomUUID(),
                LocalDate.of(2025, 3, 31),
                "SEK",
                PortfolioSource.CSV,
                List.of(h1, h2),
                new BigDecimal("24580.00"),
                List.of(new TopHolding("Qlucore", new BigDecimal("85.00"))),
                Map.of("Technology", new BigDecimal("100.00")),
                Map.of("Europe", new BigDecimal("100.00")),
                Map.of("SEK", new BigDecimal("100.00")),
                stats
        );

        assertNotNull(snapshot.id());
        assertEquals(2, snapshot.holdings().size());
        assertEquals(new BigDecimal("21100.00"), snapshot.holdings().getFirst().getMarketValue());
    }
}
