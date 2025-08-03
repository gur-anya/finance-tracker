package org.ylabHomework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.DTOs.transactionDTOs.GoalRequestDTO;
import org.ylabHomework.DTOs.transactionDTOs.GoalResponseDTO;
import org.ylabHomework.events.GoalActionEvent;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public GoalResponseDTO getUserGoal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new GoalResponseDTO(user.getGoal());
    }

    @Transactional
    public GoalResponseDTO setUserGoal(Long userId, GoalRequestDTO goalRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setGoal(goalRequestDTO.getGoal());
        return new GoalResponseDTO(user.getGoal());
    }

    @Transactional
    public void resetUserGoal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setGoal(BigDecimal.ZERO);
    }

    @Transactional
    public void clearGoalTransactions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        String GOAL_CATEGORY = "цель";
        transactionRepository.deleteByCategoryAndUserId(GOAL_CATEGORY, user.getId());
    }

    @EventListener
    public void handleGoalAction(GoalActionEvent event) {
        final BigDecimal HALF_OF_GOAL = BigDecimal.valueOf(50.0);
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        BigDecimal leftToGoal = transactionRepository.checkGoal(user.getId());

        if (leftToGoal.compareTo(HALF_OF_GOAL) > 0) {
            log.info("GOAL: you can do it, {}!", user.getEmail());
        } else if (leftToGoal.compareTo(HALF_OF_GOAL) <= 0) {
            log.info("GOAL: great job so far, {}!", user.getEmail());
        } else if (leftToGoal.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("GOAL: yaay you did it, {}! congrats!", user.getEmail());
        }
    }
}
