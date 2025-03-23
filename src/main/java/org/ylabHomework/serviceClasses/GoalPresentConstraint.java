package org.ylabHomework.serviceClasses;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class GoalPresentConstraint implements ConstraintValidator<GoalPresent, BasicTransactionDTO> {
    private final UserRepository userRepository;

    public GoalPresentConstraint(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean isValid(BasicTransactionDTO dto, ConstraintValidatorContext constraintValidatorContext) {
        String userEmail = dto.getUserEmail();
        try {
            User user = userRepository.readUserByEmail(userEmail.toLowerCase().trim());
            return user.getGoal() != 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}