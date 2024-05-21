package com.epi.epilog.app.domain.validators;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OccurenceTypeValidator implements ConstraintValidator<ValidOccurenceType, String> {

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");
    @Override
    public void initialize(ValidOccurenceType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        // Enum 값 또는 시간 포맷에 맞는지 확인
        for (OccurrenceType type : OccurrenceType.values()) {
            if (type.getDescription().equals(value)) {
                return true;
            }
        }
        return TIME_PATTERN.matcher(value).matches();
    }
}