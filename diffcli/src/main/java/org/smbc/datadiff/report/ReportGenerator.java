package org.smbc.datadiff.report;

import org.smbc.datadiff.model.ComparisonSummary;

import java.io.IOException;
import java.nio.file.Path;

public interface ReportGenerator {

    void generate(
            ComparisonSummary summary,
            Path reportFile
    );
}
