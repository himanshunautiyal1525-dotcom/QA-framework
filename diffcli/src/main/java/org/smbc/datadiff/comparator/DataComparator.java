package org.smbc.datadiff.comparator;

import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.model.ComparisonResult;

import java.nio.file.Path;

public interface DataComparator {

    ComparisonResult compare(
            Path actualFile,
            Path expectedFile,
            FileComparisonConfig config
    );
}
