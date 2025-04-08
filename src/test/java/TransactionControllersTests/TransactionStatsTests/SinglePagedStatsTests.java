package TransactionControllersTests.TransactionStatsTests;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.controllers.financeControllers.financeStatsControllers.SinglePagedStatsController;
import org.ylabHomework.models.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import org.ylabHomework.serviceClasses.GoalPresentConstraint;
import org.ylabHomework.services.TransactionStatsService;


import java.util.HashMap;
import java.util.Map;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SinglePagedStatsTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionStatsService transactionStatsService;

    @InjectMocks
    private SinglePagedStatsController singlePagedStatsController;

    private MockHttpSession session;
    private User user;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
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
        mockMvc = MockMvcBuilders.standaloneSetup(singlePagedStatsController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }

    @Test
    @DisplayName("Успешное получение баланса с положительным значением")
    public void testGetBalanceSuccess() throws Exception {
        when(transactionStatsService.calculateBalance(user)).thenReturn("Ваш баланс: 500.00 руб.");

        MvcResult result = mockMvc.perform(get("/get_balance")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        SingleParamDTO response = new SingleParamDTO(responseContent);
        assertThat(response.getParam()).contains("Ваш баланс: 500.00 руб.");
    }

    @Test
    @DisplayName("Получение баланса при нулевом значении")
    public void testGetBalanceZero() throws Exception {
        when(transactionStatsService.calculateBalance(user)).thenReturn("Ваш баланс: 0.00 руб.");

        MvcResult result = mockMvc.perform(get("/get_balance")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        SingleParamDTO response = new SingleParamDTO(responseContent);
        assertThat(response.getParam()).contains("Ваш баланс: 0.00 руб.");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Успешное получение расходов по категориям с данными")
    public void testGetExpensesByCategorySuccess() throws Exception {
        Map<String, Double> expenses = new HashMap<>();
        expenses.put("еда", 300.0);
        expenses.put("кофе", 200.0);

        when(transactionStatsService.analyzeExpenseByCategories(user)).thenReturn(expenses);

        MvcResult result = mockMvc.perform(get("/get_expenses_by_category")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Map<String, Double> response = new ObjectMapper().readValue(content, Map.class);
        assertThat(response.get("еда")).isEqualTo(300.0);
        assertThat(response.get("кофе")).isEqualTo(200.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Получение пустого списка расходов по категориям")
    public void testGetExpensesByCategoryEmpty() throws Exception {
        Map<String, Double> expenses = new HashMap<>();

        when(transactionStatsService.analyzeExpenseByCategories(user)).thenReturn(expenses);

        MvcResult result = mockMvc.perform(get("/get_expenses_by_category")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Double> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response).isEmpty();
    }
}
