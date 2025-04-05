package org.ylabHomework.serviceClasses;


import javax.validation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ylabHomework.repositories.UserRepository;

import java.sql.SQLException;
/**
 * Валидатор для аннотации проверки уникальности email пользователя.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Component
@RequiredArgsConstructor
public class UniqueConstraintUser implements ConstraintValidator<Unique, String> {
    private final UserRepository userRepository;
    private boolean uniqueRequired;
    /**
     * Метод, который получает флаг uniqueRequired (true при регистрации, false при логине).
     *
     * @param constraintAnnotation аннотация
     */
    @Override
    public void initialize(Unique constraintAnnotation) {
        this.uniqueRequired = constraintAnnotation.uniqueRequired();
    }

    /**
     * Метод валидации, который проверяет, уникален ли email пользователя. Для использования аннотации и при
     * регистрации (требуется уникальный email), и при логине (требуется неуникальный email) используется флаг
     * uniqueRequired.
     *
     * @param email проверяемый email
     * @param constraintValidatorContext контекст валидации
     * @return true, если uniqueRequired = true и email уникален, false в противном случае;
     * true, если uniqueRequired = false и email неуникален, false в противном случае
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
            try {
                if (uniqueRequired) {
                    return !(userRepository.readUserByEmail(email) == null);
                } else {
                    return userRepository.readUserByEmail(email) == null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

