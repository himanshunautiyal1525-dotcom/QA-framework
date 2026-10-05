package org.smbc.datadiff.service;

import org.smbc.datadiff.model.FilePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class FileDiscoveryService {
    public List<FilePair> discover(
            Path actualDirectory,
            Path expectedDirectory) throws IOException {

        validateDirectory(actualDirectory, "Actual");
        validateDirectory(expectedDirectory, "Expected");

        Map<String, Path> actualFiles = discoverFiles(actualDirectory);
        Map<String, Path> expectedFiles = discoverFiles(expectedDirectory);

        List<FilePair> filePairs = new ArrayList<>();

        /*
         * Process files available in actual directory.
         */
        for (Map.Entry<String, Path> entry : actualFiles.entrySet()) {

            String relativePath = entry.getKey();
            Path actualFile = entry.getValue();

            Path expectedFile = expectedFiles.get(relativePath);

            filePairs.add(
                    new FilePair(
                            relativePath,
                            actualFile,
                            expectedFile
                    )
            );
        }

        /*
         * Process files that exist only in expected directory.
         */
        for (Map.Entry<String, Path> entry : expectedFiles.entrySet()) {

            String relativePath = entry.getKey();

            if (!actualFiles.containsKey(relativePath)) {

                filePairs.add(
                        new FilePair(
                                relativePath,
                                null,
                                entry.getValue()
                        )
                );
            }
        }

        return filePairs;
    }

    private Map<String, Path> discoverFiles(Path directory)
            throws IOException {

        Map<String, Path> files = new TreeMap<>();

        try (Stream<Path> stream = Files.walk(directory)) {

            stream.filter(Files::isRegularFile)
                    .forEach(path -> {

                        String relativePath = directory
                                .relativize(path)
                                .toString()
                                .replace('\\', '/');

                        files.put(relativePath, path);
                    });
        }

        return files;
    }

    private void validateDirectory(
            Path directory,
            String directoryName) {

        if (directory == null) {
            throw new IllegalArgumentException(
                    directoryName + " directory cannot be null"
            );
        }

        if (!Files.exists(directory)) {
            throw new IllegalArgumentException(
                    directoryName + " directory does not exist: "
                            + directory
            );
        }

        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException(
                    directoryName + " path is not a directory: "
                            + directory
            );
        }
    }
}
