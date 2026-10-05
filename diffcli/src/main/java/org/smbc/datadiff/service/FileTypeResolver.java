package org.smbc.datadiff.service;

import org.smbc.datadiff.model.FileType;

import java.nio.file.Path;
import java.util.Locale;

public class FileTypeResolver {

    public FileType resolve(Path file) {
        if (file == null || file.getFileName() == null) {
            throw new IllegalArgumentException("Cannot determine file type for null file");
        }

        String fileName = file.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex < 0 || extensionIndex == fileName.length() - 1) {
            throw new IllegalArgumentException(
                    "Unable to determine file type from file name: " + fileName);
        }

        String extension = fileName.substring(extensionIndex + 1)
                .toLowerCase(Locale.ROOT);

        return switch (extension) {
            case "csv" -> FileType.CSV;
            case "json" -> FileType.JSON;
            case "xml" -> FileType.XML;
            case "txt" -> FileType.TXT;
            default -> throw new IllegalArgumentException(
                    "Unsupported file extension '" + extension + "' for file: " + fileName);
        };
    }
}
