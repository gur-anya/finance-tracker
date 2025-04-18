package org.ylabHomework.serviceClasses;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Аннотация для валидации наличия цели у пользователя.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Constraint(validatedBy = {GoalPresentConstraint.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalPresent {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
