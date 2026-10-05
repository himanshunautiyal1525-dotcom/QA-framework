package org.smbc.datadiff.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.model.DataRecord;
import org.smbc.datadiff.model.ParsedData;
import org.smbc.datadiff.model.RecordError;
import org.smbc.datadiff.model.RecordKey;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CsvDataReader implements DataReader {

    private final String source;

    public CsvDataReader() {
        this("ACTUAL");
    }

    public CsvDataReader(String source) {
        this.source = source;
    }

    @Override
    public ParsedData read(
            Path file,
            FileComparisonConfig config) throws IOException {

        validateFile(file);

        /*
         * First pass:
         * - read/validate headers
         * - identify columns that contain at least one real value
         *
         * A column with no non-blank value in any data row is removed
         * from the effective CSV structure.
         */
        List<String> headers;

        try (Reader reader = Files.newBufferedReader(
                file,
                StandardCharsets.UTF_8);
             CSVParser parser = createParser(reader)) {

            headers = new ArrayList<>(parser.getHeaderNames());
            validateHeaders(headers, file);
        }

        Set<String> nonBlankColumns =
                findNonBlankColumns(file, headers);

        /*
         * Keep the original headers for key/compare configuration validation.
         * Effective headers contain only columns with at least one value.
         */
        List<String> effectiveHeaders = headers.stream()
                .filter(nonBlankColumns::contains)
                .toList();

        List<String> keyColumns =
                determineKeyColumns(headers, effectiveHeaders, config);

        List<String> compareColumns =
                determineCompareColumns(
                        headers,
                        effectiveHeaders,
                        config,
                        keyColumns
                );

        Map<RecordKey, DataRecord> records =
                new LinkedHashMap<>();

        List<RecordError> recordErrors =
                new ArrayList<>();

        long recordCount = 0;

        /*
         * Second pass:
         * - skip completely blank rows
         * - trim values
         * - convert blank values to null
         * - normalize case for case-insensitive keys
         * - preserve existing duplicate handling
         */
        try (Reader reader = Files.newBufferedReader(
                file,
                StandardCharsets.UTF_8);
             CSVParser parser = createParser(reader)) {

            for (CSVRecord csvRecord : parser) {

                /*
                 * A completely blank/null row is not a record.
                 */
                if (isBlankRecord(csvRecord, headers)) {
                    continue;
                }

                recordCount++;

                Map<String, String> fields =
                        convertRecord(csvRecord, effectiveHeaders);

                RecordKey key =
                        buildKey(fields, keyColumns, config);

                /*
                 * Existing duplicate behavior is preserved:
                 * first occurrence participates in comparison;
                 * subsequent occurrences are record errors and are
                 * not added to the record map.
                 */
                if (records.containsKey(key)) {

                    recordErrors.add(
                            new RecordError(
                                    key.toString(),
                                    source,
                                    csvRecord.getRecordNumber(),
                                    "Duplicate key '" + key
                                            + "' found in "
                                            + source.toLowerCase()
                                            + " file"
                            )
                    );

                    continue;
                }

                records.put(
                        key,
                        new DataRecord(key, fields)
                );
            }
        }

        return new ParsedData(
                effectiveHeaders,
                keyColumns,
                compareColumns,
                records,
                recordCount,
                recordErrors
        );
    }

    private CSVParser createParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(false)
                .build()
                .parse(reader);
    }

    private void validateFile(Path file) {

        if (file == null || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException(
                    "CSV file does not exist: " + file
            );
        }
    }

    /**
     * Finds columns containing at least one non-blank value.
     *
     * A completely blank/null column is excluded from the effective CSV.
     */
    private Set<String> findNonBlankColumns(
            Path file,
            List<String> headers) throws IOException {

        Set<String> nonBlankColumns =
                new HashSet<>();

        try (Reader reader = Files.newBufferedReader(
                file,
                StandardCharsets.UTF_8);
             CSVParser parser = createParser(reader)) {

            for (CSVRecord record : parser) {

                /*
                 * Blank rows do not contribute to column presence.
                 */
                if (isBlankRecord(record, headers)) {
                    continue;
                }

                for (int index = 0;
                     index < headers.size();
                     index++) {

                    String value =
                            getValue(record, index);

                    if (value != null
                            && !value.trim().isEmpty()) {

                        nonBlankColumns.add(headers.get(index));
                    }
                }
            }
        }

        return nonBlankColumns;
    }

    /**
     * A row is blank when every column is null, empty or whitespace-only.
     */
    private boolean isBlankRecord(
            CSVRecord record,
            List<String> headers) {

        for (int index = 0;
             index < headers.size();
             index++) {

            String value =
                    getValue(record, index);

            if (value != null
                    && !value.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private String getValue(
            CSVRecord record,
            int index) {

        if (index >= record.size()) {
            return null;
        }

        return record.get(index);
    }

    private List<String> determineKeyColumns(
            List<String> headers,
            List<String> effectiveHeaders,
            FileComparisonConfig config) {

        if (config != null
                && config.getKey() != null
                && !config.getKey().isEmpty()) {

            validateColumnsExist(
                    headers,
                    config.getKey(),
                    "key"
            );

            /*
             * A configured key column cannot be completely blank.
             */
            for (String keyColumn : config.getKey()) {
                if (!effectiveHeaders.contains(keyColumn)) {
                    throw new IllegalArgumentException(
                            "Key column '" + keyColumn
                                    + "' contains no non-blank values"
                    );
                }
            }

            return List.copyOf(config.getKey());
        }

        if (headers.isEmpty()) {
            throw new IllegalArgumentException(
                    "CSV does not contain a key column"
            );
        }

        String defaultKey = headers.get(0);

        if (!effectiveHeaders.contains(defaultKey)) {
            throw new IllegalArgumentException(
                    "Default key column '" + defaultKey
                            + "' contains no non-blank values"
            );
        }

        return List.of(defaultKey);
    }

    private List<String> determineCompareColumns(
            List<String> headers,
            List<String> effectiveHeaders,
            FileComparisonConfig config,
            List<String> keyColumns) {

        if (config != null
                && config.getCompare() != null
                && !config.getCompare().isEmpty()) {

            validateColumnsExist(
                    headers,
                    config.getCompare(),
                    "compare"
            );

            /*
             * A configured compare column which is completely blank
             * is eliminated as requested.
             */
            return config.getCompare().stream()
                    .filter(effectiveHeaders::contains)
                    .toList();
        }

        Set<String> ignoredColumns =
                config != null
                        && config.getIgnore() != null
                        ? new HashSet<>(config.getIgnore())
                        : Set.of();

        List<String> compareColumns =
                new ArrayList<>();

        for (String header : effectiveHeaders) {

            if (keyColumns.contains(header)) {
                continue;
            }

            if (ignoredColumns.contains(header)) {
                continue;
            }

            compareColumns.add(header);
        }

        return compareColumns;
    }

    private Map<String, String> convertRecord(
            CSVRecord record,
            List<String> headers) {

        Map<String, String> fields =
                new LinkedHashMap<>();

        for (int index = 0;
             index < headers.size();
             index++) {

            String header = headers.get(index);

            String value =
                    normalizeValue(
                            getValue(
                                    record,
                                    index
                            )
                    );

            fields.put(header, value);
        }

        return fields;
    }

    /**
     * Trims leading/trailing spaces and converts empty values to null.
     */
    private String normalizeValue(String value) {

        if (value == null) {
            return null;
        }

        value = value.trim();

        return value.isEmpty()
                ? null
                : value;
    }

    private RecordKey buildKey(
            Map<String, String> fields,
            List<String> keyColumns,
            FileComparisonConfig config) {

        List<String> values =
                new ArrayList<>();

        for (String keyColumn : keyColumns) {

            String value =
                    fields.get(keyColumn);

            if (value == null
                    || value.isBlank()) {

                throw new IllegalArgumentException(
                        "Key column '" + keyColumn
                                + "' contains an empty value"
                );
            }

            /*
             * Key matching follows the same case-sensitivity rules
             * as the field comparison.
             */
            if (config != null
                    && !config.isCaseSensitiveFor(keyColumn)) {

                value = value.toLowerCase(Locale.ROOT);
            }

            values.add(value);
        }

        return new RecordKey(values);
    }

    private void validateHeaders(
            List<String> headers,
            Path file) {

        if (headers == null || headers.isEmpty()) {
            throw new IllegalArgumentException(
                    "CSV header is missing or empty: " + file
            );
        }

        Set<String> uniqueHeaders =
                new HashSet<>();

        for (String header : headers) {

            if (header == null
                    || header.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "CSV contains an empty column header: "
                                + file
                );
            }

            if (!uniqueHeaders.add(header)) {
                throw new IllegalArgumentException(
                        "Duplicate column header '" + header
                                + "' found in file: " + file
                );
            }
        }
    }

    private void validateColumnsExist(
            List<String> headers,
            List<String> columns,
            String configurationType) {

        for (String column : columns) {

            if (!headers.contains(column)) {

                throw new IllegalArgumentException(
                        "Configured " + configurationType
                                + " column '" + column
                                + "' does not exist in CSV header"
                );
            }
        }
    }
}
