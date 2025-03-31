package org.ylabHomework.serviceClasses;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {UniqueConstraintUser.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    boolean reversed() default false;

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}