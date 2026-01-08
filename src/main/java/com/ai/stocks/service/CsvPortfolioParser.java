package com.ai.stocks.service;

import com.ai.stocks.api.models.CsvPreviewRow;
import com.ai.stocks.api.models.CsvValidationMessage;
import com.ai.stocks.api.models.CsvValidationResponse;
import com.ai.stocks.api.models.HoldingCreateRequest;
import com.ai.stocks.api.models.SnapshotCreateRequest;
import com.ai.stocks.domain.PortfolioSource;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class CsvPortfolioParser {

    private static final Set<String> REQUIRED_COLUMNS = Set.of(
            "as_of_date",
            "instrument_name",
            "quantity",
            "price",
            "currency"
    );

    private static final Set<String> OPTIONAL_COLUMNS = Set.of(
            "ticker",
            "isin",
            "sector",
            "region"
    );

    public CsvValidationResponse validate(byte[] csvBytes, int previewLimit) {
        Objects.requireNonNull(csvBytes, "csvBytes");
        if (previewLimit <= 0) previewLimit = 20;

        List<CsvValidationMessage> messages = new ArrayList<>();
        List<CsvPreviewRow> preview = new ArrayList<>();

        try (CSVParser parser = parse(csvBytes)) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            if (headerMap == null || headerMap.isEmpty()) {
                return new CsvValidationResponse(List.of(), 0,
                        List.of(msg("ERROR", "MISSING_HEADER", "CSV header row is missing.", null)),
                        List.of(), false);
            }

            List<String> detectedCols = headerMap.keySet().stream().toList();
            Set<String> normalized = normalizeHeaders(detectedCols);

            // Required columns check
            for (String req : REQUIRED_COLUMNS) {
                if (!normalized.contains(req)) {
                    messages.add(msg("ERROR", "MISSING_REQUIRED_COLUMN",
                            "Missing required column: " + req, null));
                }
            }

            // Unknown columns warning
            for (String col : normalized) {
                if (!REQUIRED_COLUMNS.contains(col) && !OPTIONAL_COLUMNS.contains(col)) {
                    messages.add(msg("WARNING", "UNKNOWN_COLUMN",
                            "Unknown column will be ignored: " + col, null));
                }
            }

            int rowCount = 0;
            LocalDate expectedAsOf = null;
            boolean hasRowErrors = false;

            for (CSVRecord r : parser) {
                rowCount++;
                int rowNumber = (int) r.getRecordNumber() + 1; // +1 to account for header row in UX terms

                // Build preview map (raw)
                if (preview.size() < previewLimit) {
                    preview.add(new CsvPreviewRow(
                            rowNumber,
                            previewMap(r, detectedCols)
                    ));
                }

                // If header is missing required, skip per-row parsing (already invalid)
                if (messages.stream().anyMatch(m -> m.code().equals("MISSING_REQUIRED_COLUMN") && "ERROR".equals(m.severity()))) {
                    continue;
                }

                // Validate per row
                String asOfRaw = getByHeaderInsensitive(r, "as_of_date");
                LocalDate asOf = parseDate(asOfRaw, messages, rowNumber);

                if (asOf != null) {
                    if (expectedAsOf == null) expectedAsOf = asOf;
                    else if (!expectedAsOf.equals(asOf)) {
                        messages.add(msg("ERROR", "MIXED_AS_OF_DATE",
                                "All rows must have the same as_of_date. Expected " + expectedAsOf + " but got " + asOf + ".", rowNumber));
                        hasRowErrors = true;
                    }
                } else {
                    hasRowErrors = true;
                }

                String instrument = trimToNull(getByHeaderInsensitive(r, "instrument_name"));
                if (instrument == null) {
                    messages.add(msg("ERROR", "MISSING_INSTRUMENT_NAME",
                            "instrument_name is required.", rowNumber));
                    hasRowErrors = true;
                }

                BigDecimal qty = parsePositiveDecimal(getByHeaderInsensitive(r, "quantity"), "quantity", messages, rowNumber);
                BigDecimal price = parseNonNegativeDecimal(getByHeaderInsensitive(r, "price"), "price", messages, rowNumber);

                String currency = trimToNull(getByHeaderInsensitive(r, "currency"));
                if (currency == null) {
                    messages.add(msg("ERROR", "MISSING_CURRENCY",
                            "currency is required.", rowNumber));
                    hasRowErrors = true;
                }

                // Soft warnings
                if (qty != null && qty.compareTo(BigDecimal.ZERO) == 0) {
                    messages.add(msg("WARNING", "ZERO_QUANTITY",
                            "quantity is 0; this row will contribute 0 market value.", rowNumber));
                }
                if (price != null && price.compareTo(BigDecimal.ZERO) == 0) {
                    messages.add(msg("WARNING", "ZERO_PRICE",
                            "price is 0; this row will contribute 0 market value.", rowNumber));
                }
            }

            boolean valid = messages.stream().noneMatch(m -> "ERROR".equals(m.severity()));
            // If no rows at all, fail
            if (rowCount == 0) {
                valid = false;
                messages.add(msg("ERROR", "NO_ROWS", "CSV contains no data rows.", null));
            }

            return new CsvValidationResponse(
                    detectedCols,
                    rowCount,
                    messages,
                    preview,
                    valid
            );
        } catch (IOException e) {
            return new CsvValidationResponse(
                    List.of(),
                    0,
                    List.of(msg("ERROR", "CSV_READ_ERROR", "Failed to read CSV: " + e.getMessage(), null)),
                    List.of(),
                    false
            );
        }
    }

    public SnapshotCreateRequest parseToSnapshotCreateRequest(byte[] csvBytes, PortfolioSource source, String baseCurrency) {
        Objects.requireNonNull(csvBytes, "csvBytes");
        Objects.requireNonNull(source, "source");
        baseCurrency = (baseCurrency == null || baseCurrency.isBlank()) ? "SEK" : baseCurrency.trim();

        try (CSVParser parser = parse(csvBytes)) {
            List<HoldingCreateRequest> holdings = new ArrayList<>();
            LocalDate expectedAsOf = null;

            for (CSVRecord r : parser) {
                String asOfRaw = getByHeaderInsensitive(r, "as_of_date");
                LocalDate asOf = LocalDate.parse(asOfRaw.trim());
                if (expectedAsOf == null) expectedAsOf = asOf;
                else if (!expectedAsOf.equals(asOf)) {
                    throw new IllegalArgumentException("All rows must have the same as_of_date. Found " + expectedAsOf + " and " + asOf);
                }

                String instrument = getRequired(r, "instrument_name");
                BigDecimal qty = new BigDecimal(getRequired(r, "quantity"));
                BigDecimal price = new BigDecimal(getRequired(r, "price"));
                String currency = getRequired(r, "currency");

                String ticker = trimToNull(getByHeaderInsensitive(r, "ticker"));
                String isin = trimToNull(getByHeaderInsensitive(r, "isin"));
                String sector = trimToNull(getByHeaderInsensitive(r, "sector"));
                String region = trimToNull(getByHeaderInsensitive(r, "region"));

                holdings.add(new HoldingCreateRequest(
                        instrument,
                        ticker,
                        isin,
                        qty,
                        price,
                        currency,
                        sector,
                        region
                ));
            }

            if (expectedAsOf == null) {
                throw new IllegalArgumentException("CSV contains no data rows.");
            }

            return new SnapshotCreateRequest(
                    expectedAsOf,
                    baseCurrency,
                    source,
                    holdings
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read CSV: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // Parse errors (date/decimal) fall here
            throw new IllegalArgumentException("Invalid CSV content: " + e.getMessage(), e);
        }
    }

    // ---------- helpers ----------

    private CSVParser parse(byte[] csvBytes) throws IOException {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(csvBytes), StandardCharsets.UTF_8);
        return CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(reader);
    }

    private Set<String> normalizeHeaders(List<String> headers) {
        Set<String> set = new HashSet<>();
        for (String h : headers) {
            if (h == null) continue;
            set.add(h.trim().toLowerCase(Locale.ROOT));
        }
        return set;
    }

    private Map<String, String> previewMap(CSVRecord r, List<String> detectedCols) {
        Map<String, String> out = new LinkedHashMap<>();
        for (String col : detectedCols) {
            String v = "";
            try {
                v = r.isMapped(col) ? r.get(col) : "";
            } catch (Exception ignored) {}
            out.put(col, v);
        }
        return out;
    }

    private String getByHeaderInsensitive(CSVRecord r, String name) {
        // Commons CSV is header-name sensitive based on what’s in file.
        // We'll attempt case-insensitive lookup by scanning header map.
        Map<String, Integer> headers = r.getParser().getHeaderMap();
        for (String key : headers.keySet()) {
            if (key != null && key.trim().equalsIgnoreCase(name)) {
                return r.get(key);
            }
        }
        return null;
    }

    private String getRequired(CSVRecord r, String name) {
        String v = trimToNull(getByHeaderInsensitive(r, name));
        if (v == null) {
            throw new IllegalArgumentException("Missing required value for column: " + name + " at row " + (r.getRecordNumber() + 1));
        }
        return v;
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private LocalDate parseDate(String raw, List<CsvValidationMessage> messages, int rowNumber) {
        String v = trimToNull(raw);
        if (v == null) {
            messages.add(msg("ERROR", "MISSING_AS_OF_DATE", "as_of_date is required.", rowNumber));
            return null;
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            messages.add(msg("ERROR", "INVALID_DATE",
                    "as_of_date must be ISO format YYYY-MM-DD. Got: " + v, rowNumber));
            return null;
        }
    }

    private BigDecimal parsePositiveDecimal(String raw, String field, List<CsvValidationMessage> messages, int rowNumber) {
        String v = trimToNull(raw);
        if (v == null) {
            messages.add(msg("ERROR", "MISSING_" + field.toUpperCase(Locale.ROOT),
                    field + " is required.", rowNumber));
            return null;
        }
        try {
            BigDecimal d = new BigDecimal(v);
            if (d.compareTo(BigDecimal.ZERO) <= 0) {
                messages.add(msg("ERROR", "INVALID_" + field.toUpperCase(Locale.ROOT),
                        field + " must be > 0. Got: " + v, rowNumber));
                return null;
            }
            return d;
        } catch (NumberFormatException e) {
            messages.add(msg("ERROR", "INVALID_" + field.toUpperCase(Locale.ROOT),
                    field + " must be a number. Got: " + v, rowNumber));
            return null;
        }
    }

    private BigDecimal parseNonNegativeDecimal(String raw, String field, List<CsvValidationMessage> messages, int rowNumber) {
        String v = trimToNull(raw);
        if (v == null) {
            messages.add(msg("ERROR", "MISSING_" + field.toUpperCase(Locale.ROOT),
                    field + " is required.", rowNumber));
            return null;
        }
        try {
            BigDecimal d = new BigDecimal(v);
            if (d.compareTo(BigDecimal.ZERO) < 0) {
                messages.add(msg("ERROR", "INVALID_" + field.toUpperCase(Locale.ROOT),
                        field + " must be >= 0. Got: " + v, rowNumber));
                return null;
            }
            return d;
        } catch (NumberFormatException e) {
            messages.add(msg("ERROR", "INVALID_" + field.toUpperCase(Locale.ROOT),
                    field + " must be a number. Got: " + v, rowNumber));
            return null;
        }
    }

    private CsvValidationMessage msg(String severity, String code, String message, Integer rowNumber) {
        return new CsvValidationMessage(severity, code, message, rowNumber);
    }
}
