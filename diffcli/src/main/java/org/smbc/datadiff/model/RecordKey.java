package org.smbc.datadiff.model;

import java.util.List;

public record RecordKey(List<String> values) {

    public RecordKey {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Record key cannot be null or empty");
        }

        values = List.copyOf(values);
    }

    @Override
    public String toString() {
        return String.join("||", values);
    }
}
