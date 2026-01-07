package com.ai.stocks.service;

import com.ai.stocks.domain.EquityHolding;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotCalculatorTest {

    @Test
    void calculatesTotalAndTopHoldings() {
        SnapshotCalculator calc = new SnapshotCalculator();

        EquityHolding qlucore = new EquityHolding(
                "Qlucore", null, null,
                new BigDecimal("21100"),
                new BigDecimal("1.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        EquityHolding sinch = new EquityHolding(
                "Sinch", null, null,
                new BigDecimal("120"),
                new BigDecimal("29.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        var result = calc.calculate(List.of(qlucore, sinch));

        assertEquals(new BigDecimal("24580.00"), result.totalMarketValue());
        assertEquals(2, result.topHoldings().size());
        assertEquals("Qlucore", result.topHoldings().getFirst().getInstrumentName());

        // Qlucore weight ≈ 21100/24580 * 100 = 85.84%
        assertEquals(new BigDecimal("85.84"), result.topHoldings().getFirst().getWeightPercent());

        assertEquals(2, result.stats().getNumberOfHoldings());
        assertEquals(new BigDecimal("85.84"), result.stats().getTopHoldingWeightPercent());
        assertEquals(new BigDecimal("100.00"), result.stats().getTop3ConcentrationPercent());
    }

    @Test
    void groupsMissingSectorAndRegionAsUnknown() {
        SnapshotCalculator calc = new SnapshotCalculator();

        EquityHolding a = new EquityHolding(
                "A", null, null,
                new BigDecimal("10"),
                new BigDecimal("10.00"),
                "SEK",
                null,
                ""
        );

        EquityHolding b = new EquityHolding(
                "B", null, null,
                new BigDecimal("5"),
                new BigDecimal("10.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        var result = calc.calculate(List.of(a, b));

        assertTrue(result.sectorExposure().containsKey("Unknown"));
        assertTrue(result.regionExposure().containsKey("Unknown"));
        assertEquals(new BigDecimal("100.00"), result.currencyExposure().get("SEK"));
    }

    @Test
    void handlesZeroTotalValueSafely() {
        SnapshotCalculator calc = new SnapshotCalculator();

        EquityHolding zero = new EquityHolding(
                "ZeroCo", null, null,
                new BigDecimal("100"),
                new BigDecimal("0.00"),
                "SEK",
                "Technology",
                "Europe"
        );

        var result = calc.calculate(List.of(zero));

        assertEquals(new BigDecimal("0.00"), result.totalMarketValue());
        assertEquals(new BigDecimal("0.00"), result.stats().getTopHoldingWeightPercent());
        assertEquals(new BigDecimal("0.00"), result.stats().getTop3ConcentrationPercent());
    }
}