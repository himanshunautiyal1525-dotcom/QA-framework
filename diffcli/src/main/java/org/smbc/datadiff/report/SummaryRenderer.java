package org.smbc.datadiff.report;

import org.smbc.datadiff.model.ComparisonSummary;
import org.smbc.datadiff.util.HtmlUtils;

public final class SummaryRenderer {
    public void append(StringBuilder html, ComparisonSummary summary) {
        html.append("""
                <div class="summary-grid">
                """);

        appendCard(
                html,
                "Total Files",
                summary.getTotalFiles(),
                ""
        );

        appendCard(
                html,
                "Matched",
                summary.getPassedFiles(),
                "matched"
        );

        appendCard(
                html,
                "Mismatched",
                summary.getFailedFiles(),
                "mismatched"
        );

        appendCard(
                html,
                "Errors",
                summary.getErrorFiles(),
                "error"
        );

        appendCard(
                html,
                "Actual Records",
                summary.getTotalActualRecords(),
                ""
        );

        appendCard(
                html,
                "Expected Records",
                summary.getTotalExpectedRecords(),
                ""
        );

        appendCard(
                html,
                "Matched Records",
                summary.getTotalMatchedRecords(),
                ""
        );

        appendCard(
                html,
                "Added Records",
                summary.getTotalAddedRecords(),
                ""
        );

        appendCard(
                html,
                "Removed Records",
                summary.getTotalRemovedRecords(),
                ""
        );

        appendCard(
                html,
                "Modified Records",
                summary.getTotalModifiedRecords(),
                ""
        );

        appendCard(
                html,
                "Field Differences",
                summary.getTotalFieldDifferences(),
                ""
        );

        html.append("""
                </div>
                """);
    
    }

    private void appendCard(StringBuilder html, String label, long value, String cssClass) {
        html.append("<div class=\"summary-card ")
                .append(HtmlUtils.escape(cssClass))
                .append("\">");

        html.append("<div class=\"label\">")
                .append(HtmlUtils.escape(label))
                .append("</div>");

        html.append("<div class=\"value\">")
                .append(value)
                .append("</div>");

        html.append("</div>");
    
    }
}
