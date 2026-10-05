package org.smbc.datadiff.service;

import org.smbc.datadiff.config.FileComparisonConfig;
import org.smbc.datadiff.config.ReconciliationConfig;

/**
 * Resolves the effective configuration for a discovered file.
 *
 * Precedence:
 * 1. Global defaults
 * 2. File-specific configuration
 * 3. Extension-derived type when no type is configured
 *
 * For list properties, a file-level property replaces the corresponding
 * default when it is explicitly present. This also means "ignore: []" can
 * intentionally clear a global ignore list.
 */
public class ConfigurationResolver {

    public FileComparisonConfig resolve(
            String relativePath,
            ReconciliationConfig config) {

        FileComparisonConfig effective = new FileComparisonConfig();

        if (config == null) {
            return effective;
        }

        merge(effective, config.getDefaults());

        if (config.getFiles() != null) {
            merge(effective, config.getFiles().get(relativePath));
        }

        return effective;
    }

    private void merge(
            FileComparisonConfig target,
            FileComparisonConfig source) {

        if (source == null) {
            return;
        }

        if (source.getType() != null) {
            target.setType(source.getType());
        }

        if (source.getKey() != null) {
            target.setKey(source.getKey());
        }

        if (source.getIgnore() != null) {
            target.setIgnore(source.getIgnore());
        }

        if (source.getCompare() != null) {
            target.setCompare(source.getCompare());
        }

        /*
         * File-level setting overrides global setting.
         */
        if (source.getCaseSensitive() != null) {
            target.setCaseSensitive(source.getCaseSensitive());
        }

        /*
         * Explicit file-level list replaces the global list.
         * This also allows caseSensitiveColumns: [] to clear
         * the global list.
         */
        if (source.getCaseSensitiveColumns() != null) {
            target.setCaseSensitiveColumns(
                    source.getCaseSensitiveColumns()
            );
        }
    }
}
