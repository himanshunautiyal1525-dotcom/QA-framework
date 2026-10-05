package org.smbc.datadiff.model;

public record RecordError(
        String recordKey,
        String source,
        long recordNumber,
        String message
) {
}
