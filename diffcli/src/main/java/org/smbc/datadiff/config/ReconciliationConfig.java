package org.smbc.datadiff.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReconciliationConfig {

    /**
     * Global defaults applied to every configured file unless overridden by
     * that file's entry under "files".
     */
    private FileComparisonConfig defaults;

    private Map<String, FileComparisonConfig> files = new LinkedHashMap<>();

    public FileComparisonConfig getDefaults() {
        return defaults;
    }

    public void setDefaults(FileComparisonConfig defaults) {
        this.defaults = defaults;
    }

    public Map<String, FileComparisonConfig> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileComparisonConfig> files) {
        this.files = files == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(files);
    }
}
