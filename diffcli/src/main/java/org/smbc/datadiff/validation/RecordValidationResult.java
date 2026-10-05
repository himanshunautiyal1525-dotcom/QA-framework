package org.smbc.datadiff.validation;

import org.smbc.datadiff.model.RecordError;

import java.util.ArrayList;
import java.util.List;

public class RecordValidationResult {

    private final List<RecordError> errors = new ArrayList<>();

    public void addError(
            String recordKey,
            String source,
            long recordNumber,
            String message) {

        errors.add(
                new RecordError(
                        recordKey,
                        source,
                        recordNumber,
                        message
                )
        );
    }

    public void addError(RecordError error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public void addAll(List<RecordError> errors) {
        if (errors != null) {
            errors.forEach(this::addError);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int size() {
        return errors.size();
    }

    public List<RecordError> getErrors() {
        return List.copyOf(errors);
    }
}
