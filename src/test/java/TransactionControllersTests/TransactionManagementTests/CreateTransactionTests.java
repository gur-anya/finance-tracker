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
import org.ylabHomework.controllers.financeControllers.financeManagementControllers.CreateTransactionsController;
import org.ylabHomework.models.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.serviceClasses.GoalPresentConstraint;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateTransactionTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private TransactionStatsService transactionStatsService;

    @InjectMocks
    private CreateTransactionsController createTransactionsController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(createTransactionsController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }


    @Test
    @DisplayName("Успешное создание транзакции")
    public void testCreateTransactionSuccess() throws Exception {
        String jsonRequest = "{\"type\":1,\"sum\":100.0,\"category\":\"еда\",\"description\":\"перекус\"}";
        Transaction transaction = new Transaction(1, 100.0, "еда", "перекус");

        when(transactionMapper.toModel(any(BasicTransactionDTO.class))).thenReturn(transaction);

        MvcResult result = mockMvc.perform(post("/create_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Успешно!");
        verify(transactionService).createTransaction(user,1, "100.0", "еда", "перекус");
    }

    @Test
    @DisplayName("Создание транзакции с превышением бюджета")
    public void testCreateTransactionWithBudgetLimit() throws Exception {
        String jsonRequest = "{\"type\":2,\"sum\":1000.0,\"category\":\"еда\",\"description\":\"перекус\"}";
        Transaction transaction = new Transaction(2, 1000.0, "еда", "перекус");

        when(transactionMapper.toModel(any(BasicTransactionDTO.class))).thenReturn(transaction);
        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(500.0);
        when(transactionStatsService.checkMonthlyBudgetLimit(user)).thenReturn(-500.0);
        when(transactionService.notifyAboutMonthlyLimit(-500.0)).thenReturn("Превышен лимит на 500.0!");

        MvcResult result = mockMvc.perform(post("/create_transaction")
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
    @DisplayName("Неуспешное создание транзакции с некорректными данными")
    public void testCreateTransactionValidationFailure() throws Exception {
        String jsonRequest = "{\"type\":0,\"sum\":-10.0,\"category\":\"\",\"description\":\"\"}";

        MvcResult result = mockMvc.perform(post("/create_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Тип должен быть равен 1 (доход) или 2 (расход)", "Сумма должна быть больше нуля", "Категория не должна быть пустой");
    }
}