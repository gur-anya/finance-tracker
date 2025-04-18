package TransactionControllersTests.TransactionStatsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.StateAndParamDTO;
import org.ylabHomework.Main;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class MonthlyBudgetTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionStatsService transactionStatsService;

    private MockHttpSession session;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
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
