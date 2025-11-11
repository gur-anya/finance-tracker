package org.ylabHomework.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.ylabHomework.DTOs.transactionDTOs.CreateTransactionRequestDTO;
import org.ylabHomework.DTOs.transactionDTOs.CreateTransactionResponseDTO;
import org.ylabHomework.DTOs.transactionDTOs.TransactionDTO;
import org.ylabHomework.controllers.AuthController;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.UserNotFoundException;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.RoleEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;
import org.ylabHomework.serviceClasses.security.UserDetailsImpl;
import org.ylabHomework.services.*;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class TransactionServiceIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionStatisticsService transactionStatisticsService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoalService goalService;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private AuthController authController;

    @MockBean
    private TransactionService transactionService;
    private UserDetailsImpl userDetails;
    private CreateTransactionRequestDTO validRequestDTO;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("jwt.secret", () -> "test-secret-for-integration-tests");
        registry.add("frontend.url", () -> "http://localhost:3000");
    }

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();


        validRequestDTO = new CreateTransactionRequestDTO(
            TypeEnum.INCOME,
            BigDecimal.valueOf(1000),
            CategoryEnum.WAGE,
            ""
        );

        User transactionOwner = userRepository.save(new User("test", "test@example.com", "somePass", RoleEnum.USER, true, BigDecimal.ZERO, "cool goal", BigDecimal.valueOf(100000), null));
        userDetails = new UserDetailsImpl(
            transactionOwner.getId(),
            transactionOwner.getName(),
            transactionOwner.getEmail(),
            transactionOwner.getPassword(),
            transactionOwner.getRole(),
            true
        );
    }

    @Test
    void createTransaction_whenUserNotFound_shouldReturnNotFound() throws Exception {
        when(transactionService.createTransaction(any(), any())).thenThrow(new UserNotFoundException() {});
        mockMvc.perform(post("/api/v1/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO))
                .with(user(userDetails)))
            .andExpect(status().isNotFound());
    }

    @Test
    void createTransaction_whenServiceFails_shouldReturnInternalServerError() throws Exception {
        when(transactionService.createTransaction(any(), any())).thenThrow(new DataAccessException("Database connection failed") {});
            mockMvc.perform(post("/api/v1/transactions")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequestDTO))
                    .with(user(userDetails)))
                .andExpect(status().isInternalServerError());
    }
}
