package org.smbc.datadiff.factory;

import org.smbc.datadiff.comparator.DataComparator;
import org.smbc.datadiff.model.FileType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ComparatorFactory {

    private final Map<FileType, DataComparator> comparators;

    /**
     * Backward-compatible constructor for the current CSV-only application.
     */
    public ComparatorFactory(DataComparator csvComparator) {
        this(Map.of(
                FileType.CSV,
                Objects.requireNonNull(csvComparator, "csvComparator cannot be null")
        ));
    }

    /**
     * Creates a registry of comparators by file type.
     *
     * Adding a new format only requires registering its comparator here;
     * the reconciliation flow does not need another switch statement.
     */
    public ComparatorFactory(Map<FileType, DataComparator> comparators) {
        Objects.requireNonNull(comparators, "comparators cannot be null");

        EnumMap<FileType, DataComparator> registry =
                new EnumMap<>(FileType.class);

        comparators.forEach((fileType, comparator) -> {
            if (fileType == null) {
                throw new IllegalArgumentException(
                        "Comparator file type cannot be null");
            }

            registry.put(
                    fileType,
                    Objects.requireNonNull(
                            comparator,
                            "Comparator cannot be null for file type: " + fileType
                    )
            );
        });

        this.comparators = Map.copyOf(registry);
    }

    public DataComparator getComparator(FileType fileType) {
        Objects.requireNonNull(fileType, "fileType cannot be null");

        DataComparator comparator = comparators.get(fileType);

        if (comparator == null) {
            throw new UnsupportedOperationException(
                    "Comparator not implemented for file type: " + fileType
            );
        }

        return comparator;
    }
}
