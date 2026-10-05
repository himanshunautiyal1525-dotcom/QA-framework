package org.smbc.datadiff.service;

import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.config.ReconciliationConfig;

import java.util.List;

/**
 * Validates configuration rules that are independent of a particular data
 * format or file header.
 */
public class ConfigurationValidator {

    public void validate(ReconciliationConfig config) {
        if (config == null) {
            return;
        }

        validateFileConfig("defaults", config.getDefaults());

        if (config.getFiles() == null) {
            return;
        }

        ConfigurationResolver resolver = new ConfigurationResolver();

        for (var entry : config.getFiles().entrySet()) {
            validateFileConfig(
                    "files." + entry.getKey(),
                    entry.getValue()
            );

            // Validate the effective configuration too, because a file-level
            // property can conflict with a property inherited from defaults.
            validateFileConfig(
                    "effective:" + entry.getKey(),
                    resolver.resolve(entry.getKey(), config)
            );
        }
    }

    public void validate(
            String fileName,
            FileComparisonConfig config) {

        validateFileConfig(fileName, config);
    }

    private void validateFileConfig(
            String location,
            FileComparisonConfig config) {

        if (config == null) {
            return;
        }

        List<String> keyColumns = safe(config.getKey());
        List<String> ignoreColumns = safe(config.getIgnore());
        List<String> compareColumns = safe(config.getCompare());

        for (String keyColumn : keyColumns) {
            if (ignoreColumns.contains(keyColumn)) {
                throw new IllegalArgumentException(
                        "Key column '" + keyColumn
                                + "' cannot be included in ignore fields"
                                + " (configuration: " + location + ")"
                );
            }
        }

        for (String compareColumn : compareColumns) {
            if (ignoreColumns.contains(compareColumn)) {
                throw new IllegalArgumentException(
                        "Configured compare field '" + compareColumn
                                + "' cannot also be ignored"
                                + " (configuration: " + location + ")"
                );
            }
        }
    }

    private List<String> safe(List<String> values) {
        return values == null ? List.of() : values;
    }
}
