package org.smbc.datadiff.comparison;

import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.model.ComparisonResult;
import org.smbc.datadiff.model.ComparisonStatus;
import org.smbc.datadiff.model.DataRecord;
import org.smbc.datadiff.model.FieldDifference;
import org.smbc.datadiff.model.ParsedData;
import org.smbc.datadiff.model.RecordDifference;
import org.smbc.datadiff.model.RecordError;
import org.smbc.datadiff.model.RecordKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecordComparisonEngine {

    public ComparisonResult compare(
            ParsedData actualData,
            ParsedData expectedData,
            String fileName,
            org.smbc.datadiff.model.FileType fileType) {

        return compare(
                actualData,
                expectedData,
                fileName,
                fileType,
                null
        );
    }

    public ComparisonResult compare(
            ParsedData actualData,
            ParsedData expectedData,
            String fileName,
            org.smbc.datadiff.model.FileType fileType,
            FileComparisonConfig config) {

        ComparisonResult result =
                new ComparisonResult();

        result.setFileName(fileName);
        result.setFileType(fileType);

        result.setActualRecordCount(
                actualData.recordCount());

        result.setExpectedRecordCount(
                expectedData.recordCount());

        List<RecordDifference> modifiedRecords =
                new ArrayList<>();

        List<RecordDifference> addedRecords =
                new ArrayList<>();

        List<RecordDifference> removedRecords =
                new ArrayList<>();

        List<RecordError> recordErrors =
                new ArrayList<>(
                        actualData.recordErrors()
                );

        recordErrors.addAll(
                expectedData.recordErrors()
        );

        Set<RecordKey> matchedKeys =
                new HashSet<>();

        long matchedRecordCount = 0;
        long addedRecordCount = 0;
        long removedRecordCount = 0;
        long modifiedRecordCount = 0;
        long fieldDifferenceCount = 0;

        for (Map.Entry<RecordKey, DataRecord> entry
                : expectedData.records().entrySet()) {

            RecordKey key = entry.getKey();

            DataRecord expectedRecord =
                    entry.getValue();

            DataRecord actualRecord =
                    actualData.records().get(key);

            if (actualRecord == null) {

                removedRecordCount++;

                removedRecords.add(
                        new RecordDifference(
                                key.toString(),
                                createMissingActualDifferences(
                                        expectedRecord,
                                        actualData.compareColumns()
                                )
                        )
                );

                continue;
            }

            matchedKeys.add(key);

            List<FieldDifference> differences =
                    compareFields(
                            actualRecord,
                            expectedRecord,
                            actualData.compareColumns(),
                            config
                    );

            if (differences.isEmpty()) {

                matchedRecordCount++;

            } else {

                modifiedRecordCount++;
                fieldDifferenceCount +=
                        differences.size();

                modifiedRecords.add(
                        new RecordDifference(
                                key.toString(),
                                differences
                        )
                );
            }
        }

        for (Map.Entry<RecordKey, DataRecord> entry
                : actualData.records().entrySet()) {

            RecordKey key = entry.getKey();

            if (!matchedKeys.contains(key)) {

                addedRecordCount++;

                addedRecords.add(
                        new RecordDifference(
                                key.toString(),
                                createMissingExpectedDifferences(
                                        entry.getValue(),
                                        actualData.compareColumns()
                                )
                        )
                );
            }
        }

        result.setMatchedRecordCount(
                matchedRecordCount
        );

        result.setAddedRecordCount(
                addedRecordCount
        );

        result.setRemovedRecordCount(
                removedRecordCount
        );

        result.setModifiedRecordCount(
                modifiedRecordCount
        );

        result.setFieldDifferenceCount(
                fieldDifferenceCount
        );

        result.setModifiedRecords(
                modifiedRecords
        );

        result.setAddedRecords(
                addedRecords
        );

        result.setRemovedRecords(
                removedRecords
        );

        result.setRecordErrors(
                recordErrors
        );

        boolean hasDifferences =
                addedRecordCount > 0
                        || removedRecordCount > 0
                        || modifiedRecordCount > 0;

        result.setStatus(
                !recordErrors.isEmpty()
                        ? ComparisonStatus.ERROR
                        : hasDifferences
                        ? ComparisonStatus.MISMATCH
                        : ComparisonStatus.MATCH
        );

        return result;
    }

    private List<FieldDifference> compareFields(
            DataRecord actualRecord,
            DataRecord expectedRecord,
            List<String> compareColumns,
            FileComparisonConfig config) {

        List<FieldDifference> differences =
                new ArrayList<>();

        for (String column : compareColumns) {

            String actualValue =
                    actualRecord.fields().get(column);

            String expectedValue =
                    expectedRecord.fields().get(column);

            if (!valuesEqual(
                    actualValue,
                    expectedValue,
                    column,
                    config
            )) {

                differences.add(
                        new FieldDifference(
                                column,
                                actualValue,
                                expectedValue
                        )
                );
            }
        }

        return differences;
    }

    private List<FieldDifference> createMissingActualDifferences(
            DataRecord expectedRecord,
            List<String> compareColumns) {

        List<FieldDifference> differences =
                new ArrayList<>();

        for (String column : compareColumns) {

            differences.add(
                    new FieldDifference(
                            column,
                            null,
                            expectedRecord.fields().get(column)
                    )
            );
        }

        return differences;
    }

    private List<FieldDifference> createMissingExpectedDifferences(
            DataRecord actualRecord,
            List<String> compareColumns) {

        List<FieldDifference> differences =
                new ArrayList<>();

        for (String column : compareColumns) {

            differences.add(
                    new FieldDifference(
                            column,
                            actualRecord.fields().get(column),
                            null
                    )
            );
        }

        return differences;
    }

    /**
     * Compares values according to the configured column rule.
     *
     * Default:
     * - null == null
     * - null != non-null
     * - non-null values are compared case-insensitively
     *
     * Columns listed in caseSensitiveColumns are compared
     * case-sensitively.
     */
    private boolean valuesEqual(
            String actualValue,
            String expectedValue,
            String columnName,
            FileComparisonConfig config) {

        if (actualValue == null
                && expectedValue == null) {

            return true;
        }

        if (actualValue == null
                || expectedValue == null) {

            return false;
        }

        /*
         * CsvDataReader already trims values, but keep this defensive
         * normalization here so the comparison engine remains safe
         * when called directly.
         */
        actualValue = actualValue.trim();
        expectedValue = expectedValue.trim();

        boolean caseSensitive =
                config != null
                        && config.isCaseSensitiveFor(columnName);

        if (caseSensitive) {
            return actualValue.equals(expectedValue);
        }

        return actualValue.equalsIgnoreCase(expectedValue);
    }
}
