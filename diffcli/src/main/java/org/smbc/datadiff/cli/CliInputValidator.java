package org.smbc.datadiff.cli;

import java.nio.file.Files;
import java.nio.file.Path;

final class CliInputValidator {
    void validate(Path actualDirectory, Path expectedDirectory, Path configFile) {
        validateDirectory(actualDirectory, "Actual");
        validateDirectory(expectedDirectory, "Expected");

        if (configFile != null && !Files.isRegularFile(configFile)) {
            throw new IllegalArgumentException(
                    "Config file does not exist: " + configFile
            );
        }
    }

    private void validateDirectory(Path directory, String name) {
        if (directory == null) {
            throw new IllegalArgumentException(name + " directory is required");
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException(
                    name + " directory does not exist: " + directory
            );
        }
    }
}
