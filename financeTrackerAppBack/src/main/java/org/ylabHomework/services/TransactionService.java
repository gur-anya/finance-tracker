package org.ylabHomework.services;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.DTOs.transactionDTOs.*;
import org.ylabHomework.events.GoalActionTransactionEvent;
import org.ylabHomework.events.TransactionActionEvent;
import org.ylabHomework.mappers.transactionMappers.CreateTransactionMapper;
import org.ylabHomework.mappers.transactionMappers.GetAllTransactionsMapper;
import org.ylabHomework.mappers.transactionMappers.TransactionMapper;
import org.ylabHomework.mappers.transactionMappers.UpdateTransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.TransactionSpecification;
import org.ylabHomework.serviceClasses.customExceptions.NoGoalException;
import org.ylabHomework.serviceClasses.customExceptions.TransactionNotFoundException;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;

import java.math.BigDecimal;
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
    private final GetAllTransactionsMapper getAllTransactionsMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final String GOAL_CATEGORY = "ЦЕЛЬ";

    @Cacheable(cacheNames = "userTransactions")
    public GetAllTransactionsResponseDTO getAllTransactionsByUser(Long userId, Pageable pageable, FilterDTO filterDTO) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Specification<Transaction> spec = Specification.where(null);
        if (filterDTO.getType() != null) {
            spec = spec.and(TransactionSpecification.hasType(filterDTO.getType()));
        }
        if (filterDTO.getSumMoreThan() != null) {
            spec = spec.and(TransactionSpecification.sumMoreThan(filterDTO.getSumMoreThan()));
        }
        if (filterDTO.getSumLessThan() != null) {
            spec = spec.and(TransactionSpecification.sumLessThan(filterDTO.getSumLessThan()));
        }
        if (filterDTO.getStartTime() != null && filterDTO.getEndTime() != null) {
            spec = spec.and(TransactionSpecification.dateInPeriod(filterDTO.getStartTime(), filterDTO.getEndTime()));
        }
        if (filterDTO.getCategory() != null) {
            spec = spec.and(TransactionSpecification.hasCategory(filterDTO.getCategory()));
        }
        Page<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable, spec);
        return getAllTransactionsMapper.toDTO(transactions);
    }

    @CacheEvict(cacheNames = "userTransactions", key = "#userId")
    public CreateTransactionResponseDTO createTransaction(CreateTransactionRequestDTO transactionRequestDTO, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (transactionRequestDTO.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY) && user.getGoalSum().equals(BigDecimal.ZERO) && user.getGoalName().isEmpty()) {
            throw new NoGoalException();
        }


        Transaction transaction = createTransactionMapper.toModel(transactionRequestDTO);
        transaction.setUser(user);
        transactionRepository.save(transaction);

        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionTransactionEvent(transaction, user.getId()));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, user.getId()));
        }
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);


        return new CreateTransactionResponseDTO(transactionDTO);
    }

    @Transactional
    @CacheEvict(cacheNames = "userTransactions", key = "#userId")
    public UpdateTransactionResponseDTO updateTransaction(UpdateTransactionRequestDTO requestDTO, Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(TransactionNotFoundException::new);
        if (!Objects.equals(transaction.getUser().getId(), userId)) {
            throw new AccessDeniedException("Access to transaction denied");
        }
        if (requestDTO.getSum() != null) {
            transaction.setSum(requestDTO.getSum());
        }
        if (requestDTO.getCategory() != null) {
            if (!requestDTO.getCategory().isBlank()) {
                transaction.setCategory(requestDTO.getCategory().trim().toUpperCase());
            }
        }
        if (requestDTO.getDescription() != null) {
            transaction.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getType() != null) {
            transaction.setType(requestDTO.getType());
        }
        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionTransactionEvent(transaction, userId));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, userId));
        }
        return updateTransactionMapper.toDTO(transaction);
    }

    @Transactional
    @CacheEvict(cacheNames = "userTransactions", key = "#userId")
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(TransactionNotFoundException::new);
        if (!Objects.equals(transaction.getUser().getId(), userId)) {
            throw new AccessDeniedException("Access to transaction denied");
        }
        transactionRepository.deleteById(transaction.getId());
        if (transaction.getCategory().trim().equalsIgnoreCase(GOAL_CATEGORY)) {
            applicationEventPublisher.publishEvent(new GoalActionTransactionEvent(transaction, userId));
        } else {
            applicationEventPublisher.publishEvent(new TransactionActionEvent(transaction, userId));
        }
    }
}