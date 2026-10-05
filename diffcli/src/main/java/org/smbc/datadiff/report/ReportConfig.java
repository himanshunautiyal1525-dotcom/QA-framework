package org.smbc.datadiff.report;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Defines where a reconciliation report is written.
 *
 * A new timestamped report path is created for every ReportConfig instance,
 * so existing reports are never overwritten.
 */
public class ReportConfig {

    private static final String REPORT_FILE_PREFIX = "reconciliation-report-";
    private static final String REPORT_FILE_SUFFIX = ".html";
    private static final DateTimeFormatter REPORT_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final Path reportDirectory;
    private final Path reportFile;

    public ReportConfig(Path reportDirectory) {
        this.reportDirectory = reportDirectory == null
                ? Path.of(
                System.getProperty("user.home"),
                "data-reconciliation",
                "reports"
        )
                : reportDirectory;

        String timestamp = LocalDateTime.now().format(REPORT_TIMESTAMP_FORMAT);
        this.reportFile = this.reportDirectory.resolve(
                REPORT_FILE_PREFIX + timestamp + REPORT_FILE_SUFFIX
        );
    }

    public Path getReportDirectory() {
        return reportDirectory;
    }

    /**
     * Returns the report path allocated for this execution.
     * The same path is returned for the lifetime of this configuration.
     */
    public Path getReportFile() {
        return reportFile;
    }
}
