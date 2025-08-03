package org.ylabHomework.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.events.TransactionActionEvent;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;
import org.ylabHomework.serviceClasses.enums.BudgetNotificationStatus;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BigDecimal BUDGET_WARNING_PERCENTAGE = BigDecimal.valueOf(10.0);

    @EventListener
    @Transactional
    public void handleTransactionAction(TransactionActionEvent event) {
        User user = userRepository.findById(event.getUserId()).orElseThrow(UserNotFoundException::new);
        BigDecimal percentageLeftToReachLimit = transactionRepository.getPercentageLeftToReachBudgetLimit(event.getUserId());
        if (percentageLeftToReachLimit.compareTo(BUDGET_WARNING_PERCENTAGE) > 0
            && user.getBudgetNotificationStatus() != (BudgetNotificationStatus.NOT_NOTIFIED)) {
            user.setBudgetNotificationStatus(BudgetNotificationStatus.NOT_NOTIFIED);
        } else if (percentageLeftToReachLimit.compareTo(BUDGET_WARNING_PERCENTAGE) <= 0 && percentageLeftToReachLimit.compareTo(BigDecimal.ZERO) > 0) {
            if (user.getBudgetNotificationStatus() == BudgetNotificationStatus.NOT_NOTIFIED) {
                log.info("LIMIT: warning warning! {} percents to limit left fo user {}!", percentageLeftToReachLimit, user.getEmail());
                user.setBudgetNotificationStatus(BudgetNotificationStatus.WARNING_SENT);
            }
        } else if (percentageLeftToReachLimit.compareTo(BigDecimal.ZERO) <= 0) {
            if (user.getBudgetNotificationStatus() != BudgetNotificationStatus.LIMIT_EXCEEDED_SENT) {
                log.info("LIMIT: oopsie! user {} exceeded their limit!", user.getEmail());
                user.setBudgetNotificationStatus(BudgetNotificationStatus.LIMIT_EXCEEDED_SENT);
            }
        }
    }
}
