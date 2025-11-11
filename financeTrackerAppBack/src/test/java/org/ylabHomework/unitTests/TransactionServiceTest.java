package org.ylabHomework.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.ylabHomework.DTOs.transactionDTOs.*;
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
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;
import org.ylabHomework.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UpdateTransactionMapper updateTransactionMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CreateTransactionMapper createTransactionMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    private TransactionService transactionService;
    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Test
    public void createTransaction_shouldCreateRegularTransaction_andPublishEvent() {
        Long userId = 1L;

        CreateTransactionRequestDTO requestDTO = new CreateTransactionRequestDTO(
            TypeEnum.EXPENSE,
            BigDecimal.valueOf(120),
            CategoryEnum.TRANSPORT,
            ""
        );

        User mockedUser = new User();
        mockedUser.setId(userId);
        mockedUser.setEmail("test@user.com");

        Transaction transactionFromMapper = new Transaction();
        transactionFromMapper.setType(requestDTO.getType());
        transactionFromMapper.setSum(requestDTO.getSum());
        transactionFromMapper.setCategory(requestDTO.getCategory());
        transactionFromMapper.setDescription(requestDTO.getDescription());

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(100L);
        savedTransaction.setUser(mockedUser);
        savedTransaction.setType(requestDTO.getType());
        savedTransaction.setSum(requestDTO.getSum());
        savedTransaction.setCategory(requestDTO.getCategory());
        savedTransaction.setDescription(requestDTO.getDescription());
        savedTransaction.setTimestamp(LocalDateTime.now());

        TransactionDTO finalTransactionDTO = new TransactionDTO(
            100L,
            requestDTO.getType(),
            requestDTO.getSum(),
            requestDTO.getCategory(),
            requestDTO.getDescription(),
            savedTransaction.getTimestamp()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(createTransactionMapper.toModel(requestDTO)).thenReturn(transactionFromMapper);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(finalTransactionDTO);

        CreateTransactionResponseDTO response = transactionService.createTransaction(requestDTO, userId);


        assertThat(response).isNotNull();
        assertThat(response.getTransaction()).isEqualTo(finalTransactionDTO);

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction capturedForSave = transactionCaptor.getValue();

        assertThat(capturedForSave.getUser()).isEqualTo(mockedUser);
        assertThat(capturedForSave.getSum()).isEqualByComparingTo(BigDecimal.valueOf(120));
        assertThat(capturedForSave.getDescription()).isEqualTo("");
    }

    @Test
    public void createTransaction_shouldCreateGoalTransaction_whenUserHasGoal() {
        Long userId = 1L;

        CreateTransactionRequestDTO requestDTO = new CreateTransactionRequestDTO(
            TypeEnum.INCOME,
            BigDecimal.valueOf(1000),
            CategoryEnum.GOAL,
            "my goal"
        );

        User mockedUserWithGoal = new User();
        mockedUserWithGoal.setId(userId);
        mockedUserWithGoal.setGoalSum(BigDecimal.valueOf(10000));
        mockedUserWithGoal.setGoalName("Vacation");

        Transaction transactionFromMapper = new Transaction();
        transactionFromMapper.setType(requestDTO.getType());
        transactionFromMapper.setSum(requestDTO.getSum());
        transactionFromMapper.setCategory(requestDTO.getCategory());
        transactionFromMapper.setDescription(requestDTO.getDescription());

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(101L);
        savedTransaction.setUser(mockedUserWithGoal);
        savedTransaction.setType(requestDTO.getType());
        savedTransaction.setSum(requestDTO.getSum());
        savedTransaction.setCategory(requestDTO.getCategory());
        savedTransaction.setDescription(requestDTO.getDescription());
        savedTransaction.setTimestamp(LocalDateTime.now());

        TransactionDTO finalTransactionDTO = new TransactionDTO(
            101L,
            requestDTO.getType(),
            requestDTO.getSum(),
            requestDTO.getCategory(),
            requestDTO.getDescription(),
            savedTransaction.getTimestamp()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUserWithGoal));
        when(createTransactionMapper.toModel(requestDTO)).thenReturn(transactionFromMapper);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(finalTransactionDTO);

        CreateTransactionResponseDTO actualResponse = transactionService.createTransaction(requestDTO, userId);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getTransaction()).isEqualTo(finalTransactionDTO);

        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction capturedForSave = transactionCaptor.getValue();
        assertThat(capturedForSave.getUser()).isEqualTo(mockedUserWithGoal);
        assertThat(capturedForSave.getCategory()).isEqualTo(CategoryEnum.GOAL);
        assertThat(capturedForSave.getSum()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void updateTransaction_shouldUpdateFields_whenUserIsOwner() {
        Long userId = 1L;
        Long transactionId = 100L;

        UpdateTransactionRequestDTO requestDTO = new UpdateTransactionRequestDTO();
        requestDTO.setSum(BigDecimal.valueOf(100));
        requestDTO.setDescription("new description");

        User owner = new User();
        owner.setId(userId);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setUser(owner);
        existingTransaction.setSum(BigDecimal.valueOf(10));
        existingTransaction.setDescription("old description");
        existingTransaction.setCategory(CategoryEnum.TRANSPORT);
        existingTransaction.setType(TypeEnum.EXPENSE);


        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(updateTransactionMapper.toDTO(any(Transaction.class))).thenReturn(new UpdateTransactionResponseDTO());

        transactionService.updateTransaction(requestDTO, transactionId, userId);

        verify(updateTransactionMapper).toDTO(transactionCaptor.capture());
        Transaction capturedTransaction = transactionCaptor.getValue();

        assertThat(capturedTransaction.getSum()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(capturedTransaction.getDescription()).isEqualTo("new description");

        assertThat(capturedTransaction.getCategory()).isEqualTo(CategoryEnum.TRANSPORT);
        assertThat(capturedTransaction.getType()).isEqualTo(TypeEnum.EXPENSE);
        assertThat(capturedTransaction.getUser()).isEqualTo(owner);
    }

    @Test
    public void deleteTransaction_shouldDelete_whenUserIsOwner() {
        Long userId = 1L;
        Long transactionId = 100L;

        User owner = new User();
        owner.setId(userId);

        Transaction transactionToDelete = new Transaction();
        transactionToDelete.setId(transactionId);
        transactionToDelete.setUser(owner);
        transactionToDelete.setCategory(CategoryEnum.FOOD);
        transactionToDelete.setSum(BigDecimal.valueOf(500));

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionToDelete));

        transactionService.deleteTransaction(transactionId, userId);

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    public void createTransaction_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long nonExistentUserId = 999L;
        CreateTransactionRequestDTO requestDTO = new CreateTransactionRequestDTO(
            TypeEnum.EXPENSE,
            BigDecimal.valueOf(750),
            CategoryEnum.FOOD,
            "eat out"
        );

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(requestDTO, nonExistentUserId));
    }

    @Test
    public void createTransaction_shouldThrowNoGoalException_whenCreatingGoalTransactionForUserWithoutGoal() {
        Long userId = 1L;
        CreateTransactionRequestDTO requestDTO = new CreateTransactionRequestDTO(
            TypeEnum.INCOME,
            BigDecimal.valueOf(500),
            CategoryEnum.GOAL,
            "found on the streets"
        );

        User userWithoutGoal = new User();
        userWithoutGoal.setId(userId);
        userWithoutGoal.setGoalSum(BigDecimal.ZERO);
        userWithoutGoal.setGoalName("");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutGoal));

        assertThrows(NoGoalException.class, () -> transactionService.createTransaction(requestDTO, userId));
    }

    @Test
    public void updateTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        Long userId = 1L;
        Long nonExistentTransactionId = 999L;
        UpdateTransactionRequestDTO requestDTO = new UpdateTransactionRequestDTO();
        requestDTO.setSum(BigDecimal.valueOf(100));

        when(transactionRepository.findById(nonExistentTransactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.updateTransaction(requestDTO, nonExistentTransactionId, userId));
    }

    @Test
    public void updateTransaction_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        Long ownerId = 1L;
        Long attackerId = 2L;
        Long transactionId = 100L;

        UpdateTransactionRequestDTO requestDTO = new UpdateTransactionRequestDTO();
        requestDTO.setSum(BigDecimal.valueOf(100));

        User owner = new User();
        owner.setId(ownerId);


        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setUser(owner);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));

        assertThrows(AccessDeniedException.class, () -> transactionService.updateTransaction(requestDTO, transactionId, attackerId));
    }

    @Test
    public void deleteTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        Long userId = 1L;
        Long nonExistentTransactionId = 999L;

        when(transactionRepository.findById(nonExistentTransactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction(nonExistentTransactionId, userId));
    }

    @Test
    public void deleteTransaction_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        Long ownerId = 1L;
        Long attackerId = 2L;
        Long transactionId = 100L;

        User owner = new User();
        owner.setId(ownerId);

        Transaction transactionToDelete = new Transaction();
        transactionToDelete.setId(transactionId);
        transactionToDelete.setUser(owner);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionToDelete));

        assertThrows(AccessDeniedException.class, () -> transactionService.deleteTransaction(transactionId, attackerId));
    }

    @Test
    public void createTransaction_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        CreateTransactionRequestDTO requestDTO = new CreateTransactionRequestDTO(
            TypeEnum.EXPENSE,
            BigDecimal.valueOf(120),
            CategoryEnum.TRANSPORT,
            ""
        );

        User mockedUser = new User();
        mockedUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));

        when(createTransactionMapper.toModel(requestDTO)).thenReturn(new Transaction());

        when(transactionRepository.save(any(Transaction.class)))
            .thenThrow(new DataAccessException("Database connection failed") {
            });

        Assertions.assertThrows(DataAccessException.class, () -> transactionService.createTransaction(requestDTO, userId));
    }

    @Test
    public void updateTransaction_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        Long transactionId = 100L;
        UpdateTransactionRequestDTO requestDTO = new UpdateTransactionRequestDTO();
        requestDTO.setSum(BigDecimal.valueOf(100));

        User owner = new User();
        owner.setId(userId);
        Transaction existingTransaction = new Transaction();
        existingTransaction.setUser(owner);
        existingTransaction.setCategory(CategoryEnum.OTHER);

        when(transactionRepository.findById(anyLong())).thenThrow(new DataAccessException("Database connection failed") {
        });

        Assertions.assertThrows(DataAccessException.class, () -> transactionService.updateTransaction(requestDTO, transactionId, userId));
    }

    @Test
    public void deleteTransaction_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        Long transactionId = 100L;

        User owner = new User();
        owner.setId(userId);
        Transaction transactionToDelete = new Transaction();
        transactionToDelete.setId(transactionId);
        transactionToDelete.setUser(owner);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionToDelete));

        doThrow(new DataAccessException("Database connection failed") {
        })
            .when(transactionRepository).deleteById(transactionId);

        Assertions.assertThrows(DataAccessException.class, () -> transactionService.deleteTransaction(transactionId, userId));
    }
}