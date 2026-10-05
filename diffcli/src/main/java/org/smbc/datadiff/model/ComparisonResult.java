package org.smbc.datadiff.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of comparing one actual/expected file pair.
 *
 * <p>The result remains mutable while a comparator builds it, but all
 * collection values are defensively copied and exposed as immutable lists.
 * This prevents report generation or other consumers from accidentally
 * changing comparison results after the comparison has completed.</p>
 */
public class ComparisonResult {

    private String fileName;
    private FileType fileType;
    private ComparisonStatus status;
    private String errorMessage;

    private long actualRecordCount;
    private long expectedRecordCount;
    private long matchedRecordCount;
    private long addedRecordCount;
    private long removedRecordCount;
    private long modifiedRecordCount;
    private long fieldDifferenceCount;

    private List<RecordDifference> modifiedRecords = List.of();
    private List<RecordDifference> addedRecords = List.of();
    private List<RecordDifference> removedRecords = List.of();
    private List<RecordError> recordErrors = List.of();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public ComparisonStatus getStatus() {
        return status;
    }

    public void setStatus(ComparisonStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getActualRecordCount() {
        return actualRecordCount;
    }

    public void setActualRecordCount(long actualRecordCount) {
        this.actualRecordCount = actualRecordCount;
    }

    public long getExpectedRecordCount() {
        return expectedRecordCount;
    }

    public void setExpectedRecordCount(long expectedRecordCount) {
        this.expectedRecordCount = expectedRecordCount;
    }

    public long getMatchedRecordCount() {
        return matchedRecordCount;
    }

    public void setMatchedRecordCount(long matchedRecordCount) {
        this.matchedRecordCount = matchedRecordCount;
    }

    public long getAddedRecordCount() {
        return addedRecordCount;
    }

    public void setAddedRecordCount(long addedRecordCount) {
        this.addedRecordCount = addedRecordCount;
    }

    public long getRemovedRecordCount() {
        return removedRecordCount;
    }

    public void setRemovedRecordCount(long removedRecordCount) {
        this.removedRecordCount = removedRecordCount;
    }

    public long getModifiedRecordCount() {
        return modifiedRecordCount;
    }

    public void setModifiedRecordCount(long modifiedRecordCount) {
        this.modifiedRecordCount = modifiedRecordCount;
    }

    public long getFieldDifferenceCount() {
        return fieldDifferenceCount;
    }

    public void setFieldDifferenceCount(long fieldDifferenceCount) {
        this.fieldDifferenceCount = fieldDifferenceCount;
    }

    public List<RecordDifference> getModifiedRecords() {
        return modifiedRecords;
    }

    public void setModifiedRecords(List<RecordDifference> modifiedRecords) {
        this.modifiedRecords = immutableCopy(modifiedRecords);
    }

    public List<RecordDifference> getAddedRecords() {
        return addedRecords;
    }

    public void setAddedRecords(List<RecordDifference> addedRecords) {
        this.addedRecords = immutableCopy(addedRecords);
    }

    public List<RecordDifference> getRemovedRecords() {
        return removedRecords;
    }

    public void setRemovedRecords(List<RecordDifference> removedRecords) {
        this.removedRecords = immutableCopy(removedRecords);
    }

    public List<RecordError> getRecordErrors() {
        return recordErrors;
    }

    public void setRecordErrors(List<RecordError> recordErrors) {
        this.recordErrors = immutableCopy(recordErrors);
    }

    private static <T> List<T> immutableCopy(List<T> values) {
        return values == null || values.isEmpty()
                ? List.of()
                : List.copyOf(new ArrayList<>(values));
    }
}
