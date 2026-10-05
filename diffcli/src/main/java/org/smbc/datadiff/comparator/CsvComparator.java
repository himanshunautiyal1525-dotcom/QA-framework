package org.smbc.datadiff.comparator;

import org.smbc.datadiff.comparison.RecordComparisonEngine;
import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.model.ComparisonResult;
import org.smbc.datadiff.model.DataRecord;
import org.smbc.datadiff.model.FileType;
import org.smbc.datadiff.model.ParsedData;
import org.smbc.datadiff.reader.CsvDataReader;

import java.nio.file.Path;
import java.util.List;

public class CsvComparator implements DataComparator {

    private final CsvDataReader actualReader;
    private final CsvDataReader expectedReader;
    private final RecordComparisonEngine comparisonEngine;

    public CsvComparator() {
        this(
                new CsvDataReader("ACTUAL"),
                new CsvDataReader("EXPECTED"),
                new RecordComparisonEngine()
        );
    }

    public CsvComparator(
            CsvDataReader actualReader,
            CsvDataReader expectedReader,
            RecordComparisonEngine comparisonEngine) {

        this.actualReader = actualReader;
        this.expectedReader = expectedReader;
        this.comparisonEngine = comparisonEngine;
    }

    @Override
    public ComparisonResult compare(
            Path actualFile,
            Path expectedFile,
            FileComparisonConfig config) {

        String fileName =
                resolveFileName(
                        actualFile,
                        expectedFile
                );

        try {

            validateFiles(
                    actualFile,
                    expectedFile
            );

            ParsedData actualData =
                    actualReader.read(
                            actualFile,
                            config
                    );

            FileComparisonConfig expectedConfig =
                    createExpectedConfig(
                            actualData,
                            config
                    );

            ParsedData expectedData =
                    expectedReader.read(
                            expectedFile,
                            expectedConfig
                    );

            validateExpectedColumns(
                    actualData.compareColumns(),
                    expectedData.columns()
            );

            return comparisonEngine.compare(
                    actualData,
                    expectedData,
                    fileName,
                    FileType.CSV,
                    config
            );

        } catch (Exception exception) {

            ComparisonResult result =
                    new ComparisonResult();

            result.setFileName(fileName);
            result.setFileType(FileType.CSV);

            result.setStatus(
                    org.smbc.datadiff.model.ComparisonStatus.ERROR
            );

            result.setErrorMessage(
                    exception.getMessage() != null
                            ? exception.getMessage()
                            : exception.getClass().getSimpleName()
            );

            return result;
        }
    }

    private FileComparisonConfig createExpectedConfig(
            ParsedData actualData,
            FileComparisonConfig originalConfig) {

        /*
         * Copy the complete effective configuration so that
         * caseSensitive and caseSensitiveColumns are preserved.
         */
        FileComparisonConfig config =
                originalConfig != null
                        ? originalConfig.copy()
                        : new FileComparisonConfig();

        config.setType(FileType.CSV);
        config.setKey(actualData.keyColumns());
        config.setCompare(actualData.compareColumns());

        return config;
    }

    private void validateExpectedColumns(
            List<String> compareColumns,
            List<String> expectedColumns) {

        for (String column : compareColumns) {

            if (!expectedColumns.contains(column)) {

                throw new IllegalArgumentException(
                        "Configured compare column '"
                                + column
                                + "' does not exist in expected CSV header"
                );
            }
        }
    }

    private void validateFiles(
            Path actualFile,
            Path expectedFile) {

        if (actualFile == null
                || !java.nio.file.Files.isRegularFile(actualFile)) {

            throw new IllegalArgumentException(
                    "Actual file does not exist: "
                            + actualFile
            );
        }

        if (expectedFile == null
                || !java.nio.file.Files.isRegularFile(expectedFile)) {

            throw new IllegalArgumentException(
                    "Expected file does not exist: "
                            + expectedFile
            );
        }
    }

    private String resolveFileName(
            Path actualFile,
            Path expectedFile) {

        if (actualFile != null) {
            return actualFile.getFileName().toString();
        }

        if (expectedFile != null) {
            return expectedFile.getFileName().toString();
        }

        return "UNKNOWN";
    }
}
