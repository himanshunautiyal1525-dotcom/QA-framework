package org.smbc.datadiff.service;

import org.smbc.datadiff.comparator.DataComparator;
import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.config.ReconciliationConfig;
import org.smbc.datadiff.factory.ComparatorFactory;
import org.smbc.datadiff.model.ComparisonResult;
import org.smbc.datadiff.model.ComparisonStatus;
import org.smbc.datadiff.model.FilePair;
import org.smbc.datadiff.model.FileType;
import org.smbc.datadiff.model.ComparisonSummary;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ReconciliationService {

    private final FileDiscoveryService fileDiscoveryService;
    private final ComparatorFactory comparatorFactory;
    private final FileTypeResolver fileTypeResolver;
    private final ConfigurationResolver configurationResolver;
    private final ConfigurationValidator configurationValidator;

    public ReconciliationService(
            FileDiscoveryService fileDiscoveryService,
            ComparatorFactory comparatorFactory) {
        this(
                fileDiscoveryService,
                comparatorFactory,
                new FileTypeResolver(),
                new ConfigurationResolver(),
                new ConfigurationValidator()
        );
    }

    public ReconciliationService(
            FileDiscoveryService fileDiscoveryService,
            ComparatorFactory comparatorFactory,
            FileTypeResolver fileTypeResolver) {
        this(
                fileDiscoveryService,
                comparatorFactory,
                fileTypeResolver,
                new ConfigurationResolver(),
                new ConfigurationValidator()
        );
    }

    public ReconciliationService(
            FileDiscoveryService fileDiscoveryService,
            ComparatorFactory comparatorFactory,
            FileTypeResolver fileTypeResolver,
            ConfigurationResolver configurationResolver,
            ConfigurationValidator configurationValidator) {

        this.fileDiscoveryService = fileDiscoveryService;
        this.comparatorFactory = comparatorFactory;
        this.fileTypeResolver = fileTypeResolver;
        this.configurationResolver = configurationResolver;
        this.configurationValidator = configurationValidator;
    }

    /**
     * Executes reconciliation for all files found under actual and expected
     * directories.
     *
     * A failure in one file does not stop reconciliation of other files.
     */
    public ComparisonSummary reconcile(
            Path actualDirectory,
            Path expectedDirectory,
            ReconciliationConfig config) throws IOException {

        configurationValidator.validate(config);

        List<FilePair> filePairs =
                fileDiscoveryService.discover(
                        actualDirectory,
                        expectedDirectory
                );

        ComparisonSummary summary = new ComparisonSummary();

        for (FilePair filePair : filePairs) {
            ComparisonResult result;

            try {
                result = compareFile(filePair, config);
            } catch (Exception exception) {
                result = createErrorResult(
                        filePair.relativePath(),
                        "Unexpected error: " + exception.getMessage()
                );
            }

            summary.addResult(result);
        }

        return summary;
    }

    private ComparisonResult compareFile(
            FilePair filePair,
            ReconciliationConfig config) {

        if (filePair.actualFile() == null) {
            return createErrorResult(
                    filePair.relativePath(),
                    "Actual file does not exist"
            );
        }

        if (filePair.expectedFile() == null) {
            return createErrorResult(
                    filePair.relativePath(),
                    "Expected file does not exist"
            );
        }

        FileComparisonConfig fileConfig =
                getFileConfig(filePair, config);

        FileType fileType = determineFileType(
                filePair,
                fileConfig
        );

        DataComparator comparator =
                comparatorFactory.getComparator(fileType);

        return comparator.compare(
                filePair.actualFile(),
                filePair.expectedFile(),
                fileConfig
        );
    }

    private FileComparisonConfig getFileConfig(
            FilePair filePair,
            ReconciliationConfig config) {

        FileComparisonConfig fileConfig =
                configurationResolver.resolve(
                        filePair.relativePath(),
                        config
                );

        if (fileConfig.getType() == null) {
            fileConfig.setType(
                    fileTypeResolver.resolve(filePair.actualFile())
            );
        }

        return fileConfig;
    }

    private FileType determineFileType(
            FilePair filePair,
            FileComparisonConfig config) {

        // Explicit configuration always wins over the extension.
        if (config != null && config.getType() != null) {
            return config.getType();
        }

        return fileTypeResolver.resolve(filePair.actualFile());
    }

    private ComparisonResult createErrorResult(
            String fileName,
            String errorMessage) {

        ComparisonResult result = new ComparisonResult();
        result.setFileName(fileName);
        result.setStatus(ComparisonStatus.ERROR);
        result.setErrorMessage(errorMessage);

        return result;
    }
}
