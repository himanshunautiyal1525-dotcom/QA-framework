package org.smbc.datadiff.model;

import java.util.ArrayList;
import java.util.List;

public class ComparisonSummary {

    private final List<ComparisonResult> results =
            new ArrayList<>();

    public void addResult(ComparisonResult result) {
        results.add(result);
    }

    public List<ComparisonResult> getResults() {
        return List.copyOf(results);
    }

    public long getTotalFiles() {
        return results.size();
    }

    /**
     * Number of files where actual and expected data matched.
     */
    public long getPassedFiles() {

        return results.stream()
                .filter(result ->
                        result.getStatus()
                                == ComparisonStatus.MATCH)
                .count();
    }

    /**
     * Number of files where comparison completed
     * successfully but data was different.
     */
    public long getFailedFiles() {

        return results.stream()
                .filter(result ->
                        result.getStatus()
                                == ComparisonStatus.MISMATCH)
                .count();
    }

    /**
     * Number of files where comparison could not be completed.
     *
     * Examples:
     * - actual file missing
     * - expected file missing
     * - malformed CSV
     * - duplicate key
     * - invalid configuration
     * - unsupported file type
     */
    public long getErrorFiles() {

        return results.stream()
                .filter(result ->
                        result.getStatus()
                                == ComparisonStatus.ERROR)
                .count();
    }



    public long getTotalActualRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getActualRecordCount
                )
                .sum();
    }

    public long getTotalExpectedRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getExpectedRecordCount
                )
                .sum();
    }

    public long getTotalMatchedRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getMatchedRecordCount
                )
                .sum();
    }

    public long getTotalAddedRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getAddedRecordCount
                )
                .sum();
    }

    public long getTotalRemovedRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getRemovedRecordCount
                )
                .sum();
    }

    public long getTotalModifiedRecords() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getModifiedRecordCount
                )
                .sum();
    }

    public long getTotalRecordErrors() {

        return results.stream()
                .mapToLong(result ->
                        result.getRecordErrors() != null
                                ? result.getRecordErrors().size()
                                : 0
                )
                .sum();
    }

    public long getTotalFieldDifferences() {

        return results.stream()
                .mapToLong(
                        ComparisonResult::getFieldDifferenceCount
                )
                .sum();
    }
}
