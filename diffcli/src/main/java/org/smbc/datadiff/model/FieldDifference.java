package org.smbc.datadiff.model;

/**
 * Difference between actual and expected values for one field.
 */
public record FieldDifference(
        String fieldName,
        String actualValue,
        String expectedValue) {
}
