package org.smbc.datadiff.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ParsedData(
        List<String> columns,
        List<String> keyColumns,
        List<String> compareColumns,
        Map<RecordKey, DataRecord> records,
        long recordCount,
        List<RecordError> recordErrors
) {

    public ParsedData {
        columns = columns == null ? List.of() : List.copyOf(columns);
        keyColumns = keyColumns == null ? List.of() : List.copyOf(keyColumns);
        compareColumns = compareColumns == null ? List.of() : List.copyOf(compareColumns);
        records = records == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(records));
        recordErrors = recordErrors == null ? List.of() : List.copyOf(recordErrors);
    }
}
