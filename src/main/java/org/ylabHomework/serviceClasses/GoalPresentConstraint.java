package org.ylabHomework.serviceClasses;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class GoalPresentConstraint implements ConstraintValidator<GoalPresent, TransactionDTO>  {
    private final UserRepository userRepository;

    public GoalPresentConstraint() {
        this.userRepository = new UserRepository();
    }


    @Override
    public boolean isValid(TransactionDTO dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getCategory().trim().equalsIgnoreCase("цель")) {
            String userEmail = dto.getUserEmail();
            try {
                User user = userRepository.readUserByEmail(userEmail.toLowerCase().trim());
                return user.getGoal() != 0.0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return true;
        }
    }
}