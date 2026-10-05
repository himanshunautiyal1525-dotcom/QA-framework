package org.smbc.datadiff.report;

import org.smbc.datadiff.model.*;
import org.smbc.datadiff.util.HtmlUtils;

public final class FileResultRenderer {
    private final RecordRenderer recordRenderer;

    public FileResultRenderer() { this(new RecordRenderer()); }
    public FileResultRenderer(RecordRenderer recordRenderer) { this.recordRenderer = recordRenderer; }

    public void appendAll(StringBuilder html, ComparisonSummary summary) {
        for (ComparisonResult result : summary.getResults()) {
            append(html, result);
        }
    }

    public void append(StringBuilder html, ComparisonResult result) {
        html.append("""
                <details class="file-section">
                """);

        appendHeader(
                html,
                result
        );

        html.append("""
                <div class="file-body">
                """);

        if (result.getStatus()
                == ComparisonStatus.ERROR
                && result.getErrorMessage() != null
                && !result.getErrorMessage().isBlank()) {

            appendError(
                    html,
                    result
            );
        }

        /*
         * Duplicate validation errors must not hide the
         * comparison metrics or successfully calculated
         * record differences.
         */
        appendMetrics(
                html,
                result
        );

        appendRecordErrors(
                html,
                result
        );

        recordRenderer.appendModified(
                html,
                result
        );

        recordRenderer.appendAdded(
                html,
                result
        );

        recordRenderer.appendRemoved(
                html,
                result
        );

        html.append("""
                </div>
                </details>
                """);
    
    }

    private void appendHeader(StringBuilder html, ComparisonResult result) {
        String statusClass =
                statusClass(
                        result.getStatus()
                );

        String statusText =
                result.getStatus() != null
                        ? result.getStatus().name()
                        : "UNKNOWN";

        html.append("""
                <summary class="file-header">
                    <div class="file-summary">
                        <div>
                            <div class="file-summary-name">
                """);

        html.append(
                HtmlUtils.escape(
                        result.getFileName()
                )
        );

        html.append("""
                            </div>
                """);

        if (result.getFileType() != null) {

            html.append("<div>Type: ")
                    .append(
                            HtmlUtils.escape(
                                    result.getFileType().name()
                            )
                    )
                    .append("</div>");
        }

        html.append("""
                        </div>
                        <div class="file-summary-meta">
                            <span class="status
                """)
                .append(statusClass)
                .append("\">")
                .append(
                        HtmlUtils.escape(statusText)
                )
                .append("""
                            </span>
                        </div>
                    </div>
                </summary>
                """);
    
    }

    private String statusClass(ComparisonStatus status) {
        if (status == null) {
            return "status-error";
        }

        return switch (status) {

            case MATCH ->
                    "status-match";

            case MISMATCH ->
                    "status-mismatch";

            case ERROR ->
                    "status-error";
        };
    
    }

    private void appendMetrics(StringBuilder html, ComparisonResult result) {
        html.append("""
                <div class="metrics">
                """);

        appendMetric(
                html,
                "Actual Records",
                result.getActualRecordCount()
        );

        appendMetric(
                html,
                "Expected Records",
                result.getExpectedRecordCount()
        );

        appendMetric(
                html,
                "Matched",
                result.getMatchedRecordCount()
        );

        appendMetric(
                html,
                "Added",
                result.getAddedRecordCount()
        );

        appendMetric(
                html,
                "Removed",
                result.getRemovedRecordCount()
        );

        appendMetric(
                html,
                "Modified",
                result.getModifiedRecordCount()
        );

        appendMetric(
                html,
                "Field Differences",
                result.getFieldDifferenceCount()
        );

        html.append("""
                </div>
                """);
    
    }

    private void appendMetric(StringBuilder html, String label, long value) {
        html.append("""
                <div class="metric">
                """);

        html.append("<div class=\"metric-label\">")
                .append(
                        HtmlUtils.escape(label)
                )
                .append("</div>");

        html.append("<div class=\"metric-value\">")
                .append(value)
                .append("</div>");

        html.append("""
                </div>
                """);
    
    }

    private void appendRecordErrors(StringBuilder html, ComparisonResult result) {
        if (result.getRecordErrors() == null
                || result.getRecordErrors().isEmpty()) {
            return;
        }

        html.append("""
                <details class="collapsible">
                    <summary>
                        Record Errors
                        <span class="record-count">
                """)
                .append(result.getRecordErrors().size())
                .append("""
                        </span>
                    </summary>
                    <div class="collapsible-content">
                """);

        for (RecordError error :
                result.getRecordErrors()) {

            html.append("""
                    <div class="record-error">
                        <div class="record-error-header">
                            Duplicate Record: Key """)
                    .append(HtmlUtils.escape(error.recordKey()))
                    .append("""
                        </div>
                        <div class="record-error-meta">
                            Source: """)
                    .append(HtmlUtils.escape(error.source()))
                    .append(" | Record: ")
                    .append(error.recordNumber())
                    .append("""
                        </div>
                        <div>""")
                    .append(HtmlUtils.escape(error.message()))
                    .append("""
                        </div>
                    </div>
                    """);
        }

        html.append("""
                    </div>
                </details>
                """);
    
    }

    private void appendError(StringBuilder html, ComparisonResult result) {
        html.append("""
                <div class="error-message">
                    <strong>Error:</strong>
                """);

        html.append(
                HtmlUtils.escape(
                        result.getErrorMessage() != null
                                ? result.getErrorMessage()
                                : "Unknown error"
                )
        );

        html.append("""
                </div>
                """);
    
    }
}
