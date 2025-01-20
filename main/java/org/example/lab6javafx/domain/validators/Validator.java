package org.example.lab6javafx.domain.validators;

import org.example.lab6javafx.domain.validators.ValidationException;

public interface Validator<T> {
    /**
     * Validator for entities
     * It is a universal validator
     */
    void validate(T entity) throws ValidationException;
}