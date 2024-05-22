package com.epi.epilog.app.domain.validators;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OccurenceTypeValidator implements ConstraintValidator<ValidOccurenceType, OccurrenceType> {

    @Override
    public void initialize(ValidOccurenceType constraintAnnotation) {
        // 초기화 코드 (필요시)
    }

    @Override
    public boolean isValid(OccurrenceType value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 값을 허용하는 경우, 그렇지 않다면 false로 설정
        }
        // Enum 값이 유효한지 검사하는 로직
        for (OccurrenceType type : OccurrenceType.values()) {
            if (type.equals(value)) {
                return true;
            }
        }
        return false;
    }
}