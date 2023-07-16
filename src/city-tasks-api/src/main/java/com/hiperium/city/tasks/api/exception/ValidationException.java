package com.hiperium.city.tasks.api.exception;


import com.hiperium.city.tasks.api.utils.enums.EnumValidationError;

public class ValidationException extends ApplicationException {

    public ValidationException(String messageCode, Object... args) {
        super(EnumValidationError.FIELD_VALIDATION_ERROR.getCode(), messageCode, args);
    }

    public ValidationException(EnumValidationError validationError, Object... args) {
        super(validationError.getCode(), validationError.getMessage(), args);
    }
}
