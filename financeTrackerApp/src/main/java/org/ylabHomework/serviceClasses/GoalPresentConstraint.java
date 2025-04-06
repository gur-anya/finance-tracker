package org.ylabHomework.serviceClasses;

import jakarta.validation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;

import java.sql.SQLException;

/**
 * Валидатор для аннотации наличия цели у пользователя.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Component
@RequiredArgsConstructor
public class GoalPresentConstraint implements ConstraintValidator<GoalPresent, TransactionDTO>  {
    private final UserRepository userRepository;

    /**
     * Метод валидации, который проверяет, установлена ли у пользователя цель.
     *
     * @param dto DTO транзакции
     * @param constraintValidatorContext контекст валидации
     * @return true, если у пользователя установлена цель, false в противном случае
     */
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