package TransactionControllersTests.TransactionStatsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.Main;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class SinglePagedStatsTests {
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
