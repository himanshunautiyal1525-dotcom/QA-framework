package org.smbc.datadiff.cli;

import org.smbc.datadiff.model.ComparisonSummary;
import org.smbc.datadiff.report.ReportConfig;

final class ConsoleSummaryPrinter {
    void print(ComparisonSummary summary, ReportConfig reportConfig) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("       DATA RECONCILIATION COMPLETED");
        System.out.println("==============================================");
        System.out.println("Files Compared  : " + summary.getTotalFiles());
        System.out.println("Files Matched   : " + summary.getPassedFiles());
        System.out.println("Files Mismatched: " + summary.getFailedFiles());
        System.out.println("Files with Error: " + summary.getErrorFiles());
        System.out.println();
        System.out.println("Report:");
        System.out.println(reportConfig.getReportFile().toAbsolutePath());
        System.out.println("==============================================");
    }
}
