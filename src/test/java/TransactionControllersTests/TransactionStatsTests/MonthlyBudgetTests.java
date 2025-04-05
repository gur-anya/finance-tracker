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
import org.ylabHomework.DTOs.ResponseMessageDTO;

import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.StateAndParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.TransactionDTO;
import org.ylabHomework.controllers.financeControllers.financeStatsControllers.MonthlyBudgetController;
import org.ylabHomework.models.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.ylabHomework.serviceClasses.GoalPresentConstraint;
import org.ylabHomework.services.TransactionStatsService;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonthlyBudgetTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionStatsService transactionStatsService;

    @InjectMocks
    private MonthlyBudgetController monthlyBudgetController;

    private MockHttpSession session;
    private User user;

    @BeforeEach
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
        mockMvc = MockMvcBuilders.standaloneSetup(monthlyBudgetController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }

    @Test
    @DisplayName("Успешное получение бюджета")
    public void testGetMonthlyBudgetSuccess() throws Exception {
        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(2000.0);

        MvcResult result = mockMvc.perform(get("/get_monthly_budget_management")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        SingleParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), SingleParamDTO.class);
        assertThat(response.getParam()).isEqualTo("2000.0");
    }

    @Test
    @DisplayName("Получение бюджета при нулевом значении")
    public void testGetMonthlyBudgetZero() throws Exception {
        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(0.0);

        MvcResult result = mockMvc.perform(get("/get_monthly_budget_management")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        SingleParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), SingleParamDTO.class);
        assertThat(response.getParam()).isEqualTo("0.0");
    }

    @Test
    @DisplayName("Успешная проверка лимита бюджета")
    public void testCheckBudgetSuccess() throws Exception {
        when(transactionStatsService.checkMonthlyBudgetLimit(user)).thenReturn(1500.0);
        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(2000.0);

        MvcResult result = mockMvc.perform(get("/get_check_budget")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        StateAndParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StateAndParamDTO.class);
        assertThat(response.getState()).isEqualTo("1500.0");
        assertThat(response.getParam()).isEqualTo("2000.0");
    }

    @Test
    @DisplayName("Проверка лимита бюджета при SQLException")
    public void testCheckBudgetSQLException() throws Exception {
        when(transactionStatsService.checkMonthlyBudgetLimit(user)).thenReturn(0.0);
        when(transactionStatsService.getMonthlyBudget(user)).thenReturn(2000.0);

        MvcResult result = mockMvc.perform(get("/get_check_budget")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        StateAndParamDTO response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), StateAndParamDTO.class);
        assertThat(response.getState()).isEqualTo("0.0");
        assertThat(response.getParam()).isEqualTo("2000.0");
    }

    @Test
    @DisplayName("Успешное обновление бюджета")
    public void testUpdateBudgetSuccess() throws Exception {
        String jsonRequest = "{\"newValue\":2500.0}";

        MvcResult result = mockMvc.perform(post("/update_budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Успешно обновлено!");
        verify(transactionStatsService).setMonthlyBudget(user, 2500.0);
    }

    @Test
    @DisplayName("Неуспешное обновление бюджета с некорректным значением")
    public void testUpdateBudgetValidationFailure() throws Exception {
        String jsonRequest = "{\"newValue\":-200.0}";

        MvcResult result = mockMvc.perform(post("/update_budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Новое значение должно быть положительным!");
        verify(transactionStatsService, never()).setMonthlyBudget(any(User.class), anyDouble());
    }
}
