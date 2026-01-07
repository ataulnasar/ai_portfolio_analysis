package com.ai.stocks.service;

import com.ai.stocks.domain.EquityHolding;
import com.ai.stocks.domain.SnapshotStats;
import com.ai.stocks.domain.TopHolding;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SnapshotCalculator {

    // Money scale: 2 decimals (SEK), Percent scale: 2 decimals (74.53%)
    private static final int MONEY_SCALE = 2;
    private static final int PCT_SCALE = 2;

    public CalculatedSnapshot calculate(List<EquityHolding> holdings) {
        Objects.requireNonNull(holdings, "holdings");

        if (holdings.isEmpty()) {
            return new CalculatedSnapshot(
                    BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                    List.of(),
                    Map.of(),
                    Map.of(),
                    Map.of(),
                    new SnapshotStats(0, pct(0), pct(0))
            );
        }

        // Compute market values
        List<HoldingValue> values = holdings.stream()
                .map(h -> new HoldingValue(h, money(h.getMarketValue())))
                .toList();

        BigDecimal total = values.stream()
                .map(HoldingValue::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        total = money(total);

        // Avoid division by zero if all prices are 0
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return new CalculatedSnapshot(
                    total,
                    topHoldingsFrom(values, total),
                    exposurePercent(values, ExposureKey.SECTOR, total),
                    exposurePercent(values, ExposureKey.REGION, total),
                    exposurePercent(values, ExposureKey.CURRENCY, total),
                    new SnapshotStats(holdings.size(), pct(0), pct(0))
            );
        }

        List<TopHolding> topHoldings = topHoldingsFrom(values, total);

        BigDecimal top1 = topHoldings.isEmpty() ? pct(0) : topHoldings.getFirst().getWeightPercent();
        BigDecimal top3 = topHoldings.stream()
                .limit(3)
                .map(TopHolding::getWeightPercent)
                .reduce(pct(0), BigDecimal::add);
        top3 = pct(top3);

        SnapshotStats stats = new SnapshotStats(
                holdings.size(),
                top1,
                top3
        );

        Map<String, BigDecimal> sectorExposure = exposurePercent(values, ExposureKey.SECTOR, total);
        Map<String, BigDecimal> regionExposure = exposurePercent(values, ExposureKey.REGION, total);
        Map<String, BigDecimal> currencyExposure = exposurePercent(values, ExposureKey.CURRENCY, total);

        return new CalculatedSnapshot(total, topHoldings, sectorExposure, regionExposure, currencyExposure, stats);
    }

    private List<TopHolding> topHoldingsFrom(List<HoldingValue> values, BigDecimal total) {
        // Sort descending by market value
        List<HoldingValue> sorted = values.stream()
                .sorted(Comparator.comparing(HoldingValue::marketValue).reversed())
                .toList();

        return sorted.stream()
                .map(hv -> new TopHolding(
                        hv.holding().getInstrumentName(),
                        percent(hv.marketValue(), total)
                ))
                .toList();
    }

    private Map<String, BigDecimal> exposurePercent(List<HoldingValue> values, ExposureKey key, BigDecimal total) {
        // Group by exposure key, sum market values
        Map<String, BigDecimal> sums = new HashMap<>();

        for (HoldingValue hv : values) {
            String group = switch (key) {
                case SECTOR -> normalize(hv.holding().getSector());
                case REGION -> normalize(hv.holding().getRegion());
                case CURRENCY -> normalize(hv.holding().getCurrency());
            };

            sums.merge(group, hv.marketValue(), BigDecimal::add);
        }

        // Convert sums to percent of total and sort by descending exposure
        Map<String, BigDecimal> exposure = sums.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> percent(e.getValue(), total),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return exposure.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> pct(e.getValue()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) return "Unknown";
        return value.trim();
    }

    private BigDecimal percent(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return pct(0);
        // (part / total) * 100
        return part
                .multiply(new BigDecimal("100"))
                .divide(total, PCT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal v) {
        return v.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal pct(double v) {
        return BigDecimal.valueOf(v).setScale(PCT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal pct(BigDecimal v) {
        return v.setScale(PCT_SCALE, RoundingMode.HALF_UP);
    }

    private enum ExposureKey { SECTOR, REGION, CURRENCY }

    private record HoldingValue(EquityHolding holding, BigDecimal marketValue) {}

    public record CalculatedSnapshot(
            BigDecimal totalMarketValue,
            List<TopHolding> topHoldings,
            Map<String, BigDecimal> sectorExposure,
            Map<String, BigDecimal> regionExposure,
            Map<String, BigDecimal> currencyExposure,
            SnapshotStats stats
    ) {}
}
