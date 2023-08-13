package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanValidationUtil {

    public static void validateBean(TaskDTO taskDto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskDTO>> violations = validator.validate(taskDto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeanValidationUtil::throwValidationTaskDtoException);
            }
        }
    }

    public static void validateBean(TaskCriteriaDTO taskCriteriaDto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskCriteriaDTO>> violations = validator.validate(taskCriteriaDto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeanValidationUtil::throwValidationTaskCriteriaDtoException);
            }
        }
    }

    private static void throwValidationTaskDtoException(ConstraintViolation<TaskDTO> violation) {
        throw new ValidationException(violation.getMessage());
    }

    private static void throwValidationTaskCriteriaDtoException(ConstraintViolation<TaskCriteriaDTO> violation) {
        throw new ValidationException(violation.getMessage());
    }
}
