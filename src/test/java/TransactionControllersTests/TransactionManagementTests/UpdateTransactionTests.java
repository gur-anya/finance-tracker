package TransactionControllersTests.TransactionManagementTests;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.ylabHomework.DTOs.ResponseMessageDTO;

import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.controllers.financeControllers.financeManagementControllers.UpdateTransactionsController;
import org.ylabHomework.models.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.ylabHomework.models.Transaction;
import org.ylabHomework.serviceClasses.GoalPresentConstraint;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateTransactionTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionStatsService transactionStatsService;

    @InjectMocks
    private UpdateTransactionsController updateTransactionsController;
    private final MockHttpSession session = new MockHttpSession();
    private User user;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
        MockitoAnnotations.initMocks(this);

        GoalPresentConstraint mockValidator = mock(GoalPresentConstraint.class);
        when(mockValidator.isValid(any(TransactionDTO.class), any())).thenReturn(true);

        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .constraintValidatorFactory(new ConstraintValidatorFactory() {
                    @Override
                    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                        if (key == GoalPresentConstraint.class) {
                            return (T) mockValidator;
                        }
                        return new ConstraintValidatorFactoryImpl().getInstance(key);
                    }

                    @Override
                    public void releaseInstance(ConstraintValidator<?, ?> instance) {

                    }
                })
                .buildValidatorFactory()
                .getValidator();

        mockMvc = MockMvcBuilders.standaloneSetup(updateTransactionsController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }



    @Test
    @DisplayName("Успешное обновление категории транзакции")
    public void testUpdateTransactionCategorySuccess() throws Exception {
        String jsonRequest = "{\"originalType\":\"1\",\"originalSum\":100.0,\"originalCategory\":\"еда\",\"description\":\"перекус\",\"updatedValues\":\"category\",\"type\":\"1\", \"category\":\"кофе\", \"sum\":\"100.0\"}";

        MvcResult result = mockMvc.perform(post("/update_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

               String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Успешно!");
        verify(transactionService).updateTransactionCategory(any(User.class), anyString(), any(Transaction.class));
    }

    @Test
    @DisplayName("Обновление типа с превышением бюджета")
    public void testUpdateTransactionTypeWithBudgetLimit() throws Exception {
        String jsonRequest = "{\"originalType\":1,\"originalSum\":1000.0,\"originalCategory\":\"еда\",\"description\":\"перекус\",\"updatedValues\":\"type\",\"type\":\"2\", \"sum\":1000.0,\"category\":\"еда\"}";

        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(500.0);
        when(transactionStatsService.checkMonthlyBudgetLimit(user)).thenReturn(-500.0);
        when(transactionService.notifyAboutMonthlyLimit(-500.0)).thenReturn("Превышен лимит на 500.0!");

        MvcResult result = mockMvc.perform(post("/update_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

               String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Успешно! Превышен лимит на 500.0!");
    }

    @Test
    @DisplayName("Неуспешное обновление с некорректными данными")
    public void testUpdateTransactionValidationFailure() throws Exception {
        String jsonRequest = "{\"originalType\":0,\"originalSum\":-10.0,\"originalCategory\":\"\",\"description\":\"\",\"updatedValues\":\"type\"}";

        MvcResult result = mockMvc.perform(post("/update_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

               String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Тип должен быть равен 1 (доход) или 2 (расход)", "Сумма должна быть больше нуля", "Категория не должна быть пустой");
    }
    @Test
    @DisplayName("Неуспешное обновление без указания изменений")
    public void testUpdateTransactionNoChanges() throws Exception {
        String jsonRequest = "{\"originalType\":1,\"originalSum\":100.0,\"originalCategory\":\"еда\",\"description\":\"перекус\",\"updatedValues\":\"\", \"sum\":100.0,\"category\":\"еда\", \"type\":\"1\"}";

        MvcResult result = mockMvc.perform(post("/update_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Вы не сделали ни одного изменения! ");
        verify(transactionService, never()).updateTransactionType(any(User.class), anyInt(), any(Transaction.class));
        verify(transactionService, never()).updateTransactionSum(any(User.class), anyString(), any(Transaction.class));
        verify(transactionService, never()).updateTransactionCategory(any(User.class), anyString(), any(Transaction.class));
        verify(transactionService, never()).updateTransactionDescription(any(User.class), anyString(), any(Transaction.class));
    }
}