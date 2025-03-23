package org.ylabHomework.serviceClasses;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ylabHomework.repositories.UserRepository;

import java.sql.SQLException;

public class UniqueConstraintUser implements ConstraintValidator<Unique, String> {
    private final UserRepository userRepository;
    private boolean checkHabit;
    private boolean reversed;

    public UniqueConstraintUser() {
        this.userRepository = new UserRepository();
    }

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.reversed = constraintAnnotation.reversed();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (checkHabit) {
            return true;
        } else {
            try {
                if (reversed) {
                    return !(userRepository.readUserByEmail(value) == null);
                } else {
                    return userRepository.readUserByEmail(value) == null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
