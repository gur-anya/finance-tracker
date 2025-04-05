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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.controllers.financeControllers.financeStatsControllers.SummaryIncomeExpenseController;
import org.ylabHomework.models.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.ylabHomework.serviceClasses.GoalPresentConstraint;
import org.ylabHomework.services.TransactionStatsService;


import java.time.LocalDateTime;
import java.util.Map;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SummaryIncomesExpensesTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionStatsService transactionStatsService;

    @InjectMocks
    private SummaryIncomeExpenseController summaryIncomeExpenseController;

    private MockHttpSession session;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        User user = new User("anya", "anya@ya.ru", "1234", 1);
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
        mockMvc = MockMvcBuilders.standaloneSetup(summaryIncomeExpenseController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Успешное получение доходов и расходов за период")
    public void testGetIncomeExpenseForPeriodSuccess() throws Exception {
        String jsonRequest = "{\"start\":\"2025-04-01T10:00\",\"end\":\"2025-04-02T10:00\"}";
        double[] stats = new double[]{1000.0, 500.0, 500.0};

        when(transactionStatsService.getIncomeExpenseForPeriod(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(stats);

        MvcResult result = mockMvc.perform(post("/summary_income_expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Double> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("income")).isEqualTo(1000.0);
        assertThat(response.get("expense")).isEqualTo(500.0);
        assertThat(response.get("balance")).isEqualTo(500.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Получение нулевых значений при отсутствии данных")
    public void testGetIncomeExpenseForPeriodNoData() throws Exception {
        String jsonRequest = "{\"start\":\"2025-04-01T10:00\",\"end\":\"2025-04-02T10:00\"}";
        double[] stats = new double[]{0.0, 0.0, 0.0};

        when(transactionStatsService.getIncomeExpenseForPeriod(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(stats);

        MvcResult result = mockMvc.perform(post("/summary_income_expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Double> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(response.get("income")).isEqualTo(0.0);
        assertThat(response.get("expense")).isEqualTo(0.0);
        assertThat(response.get("balance")).isEqualTo(0.0);
    }
}
