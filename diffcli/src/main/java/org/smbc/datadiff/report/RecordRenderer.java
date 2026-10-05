package org.smbc.datadiff.report;

import org.smbc.datadiff.model.*;
import org.smbc.datadiff.util.HtmlUtils;

public final class RecordRenderer {
    public void appendModified(StringBuilder html, ComparisonResult result) {
        if (result.getModifiedRecords() == null
                || result.getModifiedRecords().isEmpty()) {
            return;
        }

        html.append("""
                <details class="collapsible">
                    <summary>
                        Modified Records
                        <span class="record-count">
                """)
                .append(result.getModifiedRecords().size())
                .append("""
                        </span>
                    </summary>
                    <div class="collapsible-content">
                """);

        for (RecordDifference record :
                result.getModifiedRecords()) {

            append(
                    html,
                    record
            );
        }

        html.append("""
                    </div>
                </details>
                """);
    
    }

    public void appendAdded(StringBuilder html, ComparisonResult result) {
        if (result.getAddedRecords() == null
                || result.getAddedRecords().isEmpty()) {
            return;
        }

        html.append("""
                <details class="collapsible">
                    <summary>
                        Added Records
                        <span class="record-count">
                """)
                .append(result.getAddedRecords().size())
                .append("""
                        </span>
                    </summary>
                    <div class="collapsible-content">
                """);

        for (RecordDifference record :
                result.getAddedRecords()) {

            append(
                    html,
                    record
            );
        }

        html.append("""
                    </div>
                </details>
                """);
    
    }

    public void appendRemoved(StringBuilder html, ComparisonResult result) {
        if (result.getRemovedRecords() == null
                || result.getRemovedRecords().isEmpty()) {
            return;
        }

        html.append("""
                <details class="collapsible">
                    <summary>
                        Removed Records
                        <span class="record-count">
                """)
                .append(result.getRemovedRecords().size())
                .append("""
                        </span>
                    </summary>
                    <div class="collapsible-content">
                """);

        for (RecordDifference record :
                result.getRemovedRecords()) {

            append(
                    html,
                    record
            );
        }

        html.append("""
                    </div>
                </details>
                """);
    
    }

    private void append(StringBuilder html, RecordDifference record) {
        int differenceCount =
                record.fieldDifferences() != null
                        ? record.fieldDifferences().size()
                        : 0;

        html.append("""
                <details class="record record-details">
                    <summary class="record-header">
                        <span>Key:
                """)
                .append(
                        HtmlUtils.escape(
                                record.recordKey()
                        )
                )
                .append("""
                        </span>
                        <span class="record-count">
                """)
                .append(differenceCount)
                .append(
                        differenceCount == 1
                                ? " field difference"
                                : " field differences"
                )
                .append("""
                        </span>
                    </summary>
                    <div class="collapsible-content">
                        <table>
                            <thead>
                                <tr>
                                    <th>Field</th>
                                    <th>Actual</th>
                                    <th>Expected</th>
                                </tr>
                            </thead>
                            <tbody>
                """);

        if (record.fieldDifferences() != null) {

            for (FieldDifference difference :
                    record.fieldDifferences()) {

                appendFieldDifference(
                        html,
                        difference
                );
            }
        }

        html.append("""
                            </tbody>
                        </table>
                    </div>
                </details>
                """);
    
    }

    private void appendFieldDifference(StringBuilder html, FieldDifference difference) {
        html.append("<tr>");

        html.append("<td>")
                .append(
                        HtmlUtils.escape(
                                difference.fieldName()
                        )
                )
                .append("</td>");

        html.append("<td class=\"actual\">");

        appendValue(
                html,
                difference.actualValue()
        );

        html.append("</td>");

        html.append("<td class=\"expected\">");

        appendValue(
                html,
                difference.expectedValue()
        );

        html.append("</td>");

        html.append("</tr>");
    
    }

    private void appendValue(StringBuilder html, String value) {
        if (value == null) {

            html.append(
                    "<span class=\"empty-value\">-</span>"
            );

        } else {

            html.append(
                    HtmlUtils.escape(value)
            );
        }
    
    }
}
