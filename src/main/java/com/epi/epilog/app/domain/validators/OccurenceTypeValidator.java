package com.epi.epilog.app.domain.validators;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OccurenceTypeValidator implements ConstraintValidator<ValidOccurenceType, String> {

    private static final Pattern TIME_PATTERN = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$");

    @Override
    public void initialize(ValidOccurenceType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // null 값은 유효하지 않음
        }
        // Enum 값이거나 시간 형식인지 검사
        return OccurrenceType.isValid(value) || TIME_PATTERN.matcher(value).matches();
    }
}