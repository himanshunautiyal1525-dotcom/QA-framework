package org.smbc.datadiff.cli;

import org.smbc.datadiff.comparator.CsvComparator;
import org.smbc.datadiff.config.ConfigLoader;
import org.smbc.datadiff.config.ReconciliationConfig;
import org.smbc.datadiff.factory.ComparatorFactory;
import org.smbc.datadiff.model.ComparisonSummary;
import org.smbc.datadiff.report.HtmlReportGenerator;
import org.smbc.datadiff.report.ReportConfig;
import org.smbc.datadiff.report.ReportGenerator;
import org.smbc.datadiff.service.FileDiscoveryService;
import org.smbc.datadiff.service.ReconciliationService;

import java.io.IOException;
import java.nio.file.Path;

/** Application-level orchestration kept outside the Picocli command. */
final class ReconciliationRunner {
    private final CliInputValidator inputValidator;
    private final ConfigLoader configLoader;
    private final ReconciliationService reconciliationService;
    private final ReportGenerator reportGenerator;
    private final ConsoleSummaryPrinter summaryPrinter;

    ReconciliationRunner() {
        this(
                new CliInputValidator(),
                new ConfigLoader(),
                createReconciliationService(),
                new HtmlReportGenerator(),
                new ConsoleSummaryPrinter()
        );
    }

    ReconciliationRunner(
            CliInputValidator inputValidator,
            ConfigLoader configLoader,
            ReconciliationService reconciliationService,
            ReportGenerator reportGenerator,
            ConsoleSummaryPrinter summaryPrinter) {
        this.inputValidator = inputValidator;
        this.configLoader = configLoader;
        this.reconciliationService = reconciliationService;
        this.reportGenerator = reportGenerator;
        this.summaryPrinter = summaryPrinter;
    }

    void execute(
            Path actualDirectory,
            Path expectedDirectory,
            Path configFile,
            Path reportDirectory) throws IOException {

        inputValidator.validate(
                actualDirectory,
                expectedDirectory,
                configFile
        );

        ReconciliationConfig config = configLoader.load(configFile);

        ComparisonSummary summary =
                reconciliationService.reconcile(
                        actualDirectory,
                        expectedDirectory,
                        config
                );

        ReportConfig reportConfig = new ReportConfig(reportDirectory);

        reportGenerator.generate(
                summary,
                reportConfig.getReportFile()
        );

        summaryPrinter.print(summary, reportConfig);
    }

    private static ReconciliationService createReconciliationService() {
        ComparatorFactory comparatorFactory =
                new ComparatorFactory(new CsvComparator());

        return new ReconciliationService(
                new FileDiscoveryService(),
                comparatorFactory
        );
    }
}
