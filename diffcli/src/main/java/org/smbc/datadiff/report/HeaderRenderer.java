package org.smbc.datadiff.report;

public final class HeaderRenderer {
    public void append(StringBuilder html) {
        html.append("""
                <div class="header">
                    <h1>Data Reconciliation Report</h1>
                    <p>Actual vs Expected EOD Data Comparison</p>
                </div>
                """);
    
    }
}
