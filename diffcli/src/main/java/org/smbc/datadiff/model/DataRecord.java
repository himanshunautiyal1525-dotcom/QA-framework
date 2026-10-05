package org.smbc.datadiff.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record DataRecord(
        RecordKey key,
        Map<String, String> fields
) {

    public DataRecord {
        if (key == null) {
            throw new IllegalArgumentException("Record key cannot be null");
        }

        if (fields == null) {
            throw new IllegalArgumentException("Record fields cannot be null");
        }

        fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
    }
}
