package com.epi.epilog.app.domain.annotations;

import com.epi.epilog.app.domain.validators.OccurenceTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OccurenceTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOccurenceType {
    String message() default "Invalid Occurrence Type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
