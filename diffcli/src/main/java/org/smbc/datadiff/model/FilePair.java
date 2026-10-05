package org.smbc.datadiff.model;

import java.nio.file.Path;

public record FilePair(String relativePath,
                       Path actualFile,
                       Path expectedFile) {
}
