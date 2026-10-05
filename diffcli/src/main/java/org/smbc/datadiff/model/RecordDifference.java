package org.smbc.datadiff.model;

import java.util.List;

/**
 * Difference information for a single record.
 */
public record RecordDifference(
        String recordKey,
        List<FieldDifference> fieldDifferences) {

    public RecordDifference {
        fieldDifferences = fieldDifferences == null
                ? List.of()
                : List.copyOf(fieldDifferences);
    }
}
