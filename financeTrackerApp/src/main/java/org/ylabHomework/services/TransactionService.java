package org.ylabHomework.services;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.DTOs.transactionDTOs.*;
import org.ylabHomework.events.GoalActionEvent;
import org.ylabHomework.events.TransactionActionEvent;
import org.ylabHomework.mappers.transactionMappers.CreateTransactionMapper;
import org.ylabHomework.mappers.transactionMappers.TransactionMapper;
import org.ylabHomework.mappers.transactionMappers.UpdateTransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.NoGoalException;
import org.ylabHomework.serviceClasses.customExceptions.TransactionNotFoundException;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;

import java.util.Objects;

/**
 * Сервис для работы с сущностью Transaction.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 02.08.2025
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CreateTransactionMapper createTransactionMapper;
    private final UpdateTransactionMapper updateTransactionMapper;
    private final TransactionMapper transactionMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final String GOAL_CATEGORY = "цель";

    public CreateTransactionResponseDTO createTransaction(CreateTransactionRequestDTO transactionRequestDTO, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (transactionRequestDTO.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            throw new NoGoalException();
        }

        Transaction transaction = createTransactionMapper.toModel(transactionRequestDTO);

        transactionRepository.save(transaction);

        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionEvent(transaction, user.getId()));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, user.getId()));
        }
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);


        return new CreateTransactionResponseDTO(transactionDTO);
    }

    @Transactional
    public UpdateTransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO requestDTO, Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(TransactionNotFoundException::new);
        if (!Objects.equals(transaction.getUser().getId(), userId)) {
            throw new AccessDeniedException("Access denied");
        }
        if (requestDTO.getSum() != null) {
            transaction.setSum(requestDTO.getSum());
        }
        if (requestDTO.getCategory() != null) {
            if (!requestDTO.getCategory().isBlank()) {
                transaction.setCategory(requestDTO.getCategory().trim().toLowerCase());
            }
        }
        if (requestDTO.getDescription() != null) {
            transaction.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getType() != null) {
            transaction.setType(requestDTO.getType());
        }
        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionEvent(transaction, userId));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, userId));
        }
        return updateTransactionMapper.toDTO(transaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(TransactionNotFoundException::new);
        if (!Objects.equals(transaction.getUser().getId(), userId)) {
            throw new AccessDeniedException("Access denied");
        }
        transactionRepository.deleteById(transaction.getId());
        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionEvent(transaction, userId));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, userId));
        }
    }
}