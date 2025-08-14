package org.ylabHomework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.CheckGoalResponseDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalRequestDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalResponseDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.UpdateGoalRequestDTO;
import org.ylabHomework.events.GoalActionTransactionEvent;
import org.ylabHomework.events.GoalActionUpdateEvent;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GoalResponseDTO getUserGoal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new GoalResponseDTO(user.getGoalName(), user.getGoalSum());
    }

    @Transactional
    public GoalResponseDTO setUserGoal(Long userId, GoalRequestDTO goalRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setGoalName(goalRequestDTO.getGoalName());
        user.setGoalSum(goalRequestDTO.getGoalSum());
        return new GoalResponseDTO(user.getGoalName(), user.getGoalSum());
    }

    @Transactional
    public GoalResponseDTO updateUserGoal(Long userId, UpdateGoalRequestDTO updateRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (updateRequestDTO.getGoalName() != null && !updateRequestDTO.getGoalName().isEmpty()) {
            user.setGoalName(updateRequestDTO.getGoalName());
        }
        if (updateRequestDTO.getGoalSum() != null) {
            user.setGoalSum(updateRequestDTO.getGoalSum());
        }
        applicationEventPublisher.publishEvent(new GoalActionUpdateEvent(userId));
        return new GoalResponseDTO(user.getGoalName(), user.getGoalSum());
    }

    @Transactional
    public void resetUserGoal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setGoalSum(BigDecimal.ZERO);
        user.setGoalName("");
    }

    @Transactional
    public void clearGoalTransactions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        transactionRepository.deleteByCategoryAndUserId(CategoryEnum.GOAL, user.getId());
    }

    public CheckGoalResponseDTO checkSavedToGoal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        BigDecimal savedForGoal = transactionRepository.checkSavedToGoal(user.getId())
            .orElse(BigDecimal.ZERO);
        return new CheckGoalResponseDTO(savedForGoal);
    }

    @EventListener
    public void handleGoalAction(GoalActionTransactionEvent event) {
        final BigDecimal HALF_OF_GOAL = BigDecimal.valueOf(50.0);
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        BigDecimal leftToGoal = transactionRepository.checkLeftToGoal(user.getId())
            .orElse(BigDecimal.ZERO);

        if (leftToGoal.compareTo(HALF_OF_GOAL) > 0) {
            log.info("GOAL: you can do it, {}!", user.getEmail());
        } else if (leftToGoal.compareTo(HALF_OF_GOAL) <= 0) {
            log.info("GOAL: great job so far, {}!", user.getEmail());
        } else if (leftToGoal.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("GOAL: yaay you did it, {}! congrats!", user.getEmail());
        }
    }
}
