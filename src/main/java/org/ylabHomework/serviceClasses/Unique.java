package org.ylabHomework.serviceClasses;

import jakarta.validation.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Аннотация для проверки уникальности email пользователя.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Constraint(validatedBy = {UniqueConstraintUser.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    boolean uniqueRequired() default false;

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}