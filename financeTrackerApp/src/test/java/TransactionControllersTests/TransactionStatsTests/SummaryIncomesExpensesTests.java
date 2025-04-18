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
import org.ylabHomework.Main;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class SummaryIncomesExpensesTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionStatsService transactionStatsService;

    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        User user = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
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
