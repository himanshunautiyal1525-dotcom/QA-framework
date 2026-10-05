package org.smbc.datadiff.reader;

import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.model.ParsedData;

import java.io.IOException;
import java.nio.file.Path;

public interface DataReader {

    ParsedData read(
            Path file,
            FileComparisonConfig config
    ) throws IOException;
}
