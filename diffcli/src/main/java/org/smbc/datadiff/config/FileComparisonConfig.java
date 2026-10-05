package org.smbc.datadiff.config;

import org.smbc.datadiff.model.FileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration applicable to a single file.
 *
 * A null property means "not specified", which is important when merging
 * file-specific configuration with the global defaults section.
 *
 * Case comparison rules:
 * 1. caseSensitiveColumns has highest priority and is always case-sensitive.
 * 2. caseSensitive controls all other columns.
 * 3. If neither is configured, comparison is case-insensitive.
 */
public class FileComparisonConfig {

    private FileType type;
    private List<String> key;
    private List<String> ignore;
    private List<String> compare;

    /**
     * Default case-sensitivity for all columns not listed in
     * caseSensitiveColumns.
     */
    private Boolean caseSensitive;

    /**
     * Columns which must be compared case-sensitively.
     *
     * Example:
     * caseSensitiveColumns:
     *   - ACCOUNT_ID
     *   - ACCOUNT_STATUS
     */
    private List<String> caseSensitiveColumns;

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = copy(key);
    }

    public List<String> getIgnore() {
        return ignore;
    }

    public void setIgnore(List<String> ignore) {
        this.ignore = copy(ignore);
    }

    public List<String> getCompare() {
        return compare;
    }

    public void setCompare(List<String> compare) {
        this.compare = copy(compare);
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public List<String> getCaseSensitiveColumns() {
        return caseSensitiveColumns;
    }

    public void setCaseSensitiveColumns(List<String> caseSensitiveColumns) {
        this.caseSensitiveColumns = copy(caseSensitiveColumns);
    }

    /**
     * Returns whether a particular column should be compared
     * case-sensitively.
     *
     * Selected columns always win over the file/global setting.
     */
    public boolean isCaseSensitiveFor(String columnName) {

        if (caseSensitiveColumns != null
                && caseSensitiveColumns.contains(columnName)) {
            return true;
        }

        /*
         * Everything is case-insensitive by default.
         */
        return caseSensitive != null && caseSensitive;
    }

    /**
     * Creates a detached copy so merged configuration does not share mutable
     * lists with the YAML-deserialized objects.
     */
    public FileComparisonConfig copy() {
        FileComparisonConfig copy = new FileComparisonConfig();
        copy.setType(type);
        copy.setKey(key);
        copy.setIgnore(ignore);
        copy.setCompare(compare);
        copy.setCaseSensitive(caseSensitive);
        copy.setCaseSensitiveColumns(caseSensitiveColumns);
        return copy;
    }

    private static List<String> copy(List<String> values) {
        return values == null ? null : new ArrayList<>(values);
    }
}
