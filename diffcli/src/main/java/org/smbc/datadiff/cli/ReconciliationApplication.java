package org.smbc.datadiff.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "data-reconciliation",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Compares actual and expected EOD data files."
)
public class ReconciliationApplication implements Callable<Integer> {

    @Option(
            names = "--actual",
            description = "Directory containing actual files",
            required = true
    )
    private Path actualDirectory;

    @Option(
            names = "--expected",
            description = "Directory containing expected files",
            required = true
    )
    private Path expectedDirectory;

    @Option(
            names = "--config",
            description = "Optional YAML configuration file"
    )
    private Path configFile;

    @Option(
            names = "--report-dir",
            description = "Optional directory for the HTML report"
    )
    private Path reportDirectory;

    private final ReconciliationRunner runner;

    public ReconciliationApplication() {
        this(new ReconciliationRunner());
    }

    ReconciliationApplication(ReconciliationRunner runner) {
        this.runner = runner;
    }

    @Override
    public Integer call() {
        try {
            runner.execute(
                    actualDirectory,
                    expectedDirectory,
                    configFile,
                    reportDirectory
            );
            return 0;
        } catch (Exception exception) {
            System.err.println();
            System.err.println(
                    "Reconciliation failed: " + exception.getMessage()
            );
            return 2;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(
                new ReconciliationApplication()
        ).execute(args);
        System.exit(exitCode);
    }
}
