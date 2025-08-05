package org.ylabHomework.serviceClasses;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Пароль должен содержать хотя бы одну заглавную букву, одну строчную, одну цифру и один специальный символ!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}