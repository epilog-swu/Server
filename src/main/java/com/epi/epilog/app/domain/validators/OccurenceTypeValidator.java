package com.epi.epilog.app.domain.validators;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.log.OccurrenceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OccurenceTypeValidator implements ConstraintValidator<ValidOccurenceType, String> {

    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):(0[1-9]|[0-5][0-9]):(0[1-9]|[0-5][0-9])$");

    @Override
    public void initialize(ValidOccurenceType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return OccurrenceType.isValid(value) || TIME_PATTERN.matcher(value).matches();
    }
}