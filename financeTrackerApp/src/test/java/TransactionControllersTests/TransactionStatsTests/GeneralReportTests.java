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
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class GeneralReportTests {
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
    @DisplayName("Успешная генерация отчета с указанием start и end")
    public void testGenerateReportWithStartAndEnd() throws Exception {
        String jsonRequest = "{\"start\":\"2025-04-01T10:00\",\"end\":\"2025-04-02T10:00\"}";
        TransactionStatsService.FinancialReport report = new TransactionStatsService.FinancialReport(
                1000.0, 500.0, 500.0, new HashMap<>(), new double[]{1000.0, 200.0, 300.0, 400.0, 500.0}
        );

        when(transactionStatsService.generateGeneralReport(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(report);

        MvcResult result = mockMvc.perform(post("/general_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Double> basicStats = (Map<String, Double>) response.get("basicStats");
        Map<String, Double> goalData = (Map<String, Double>) response.get("goalData");

        assertThat(basicStats.get("totalIncome")).isEqualTo(1000.0);
        assertThat(basicStats.get("totalExpense")).isEqualTo(500.0);
        assertThat(basicStats.get("totalBalance")).isEqualTo(500.0);
        assertThat(goalData.get("goalSum")).isEqualTo(1000.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Успешная генерация отчета с указанием только start")
    public void testGenerateReportWithOnlyStart() throws Exception {
        String jsonRequest = "{\"start\":\"2025-04-01T10:00\",\"end\":\"\"}";
        TransactionStatsService.FinancialReport report = new TransactionStatsService.FinancialReport(
                800.0, 300.0, 500.0, new HashMap<>(), new double[]{900.0, 100.0, 200.0, 300.0, 400.0}
        );

        when(transactionStatsService.generateGeneralReport(any(User.class), any(LocalDateTime.class), isNull())).thenReturn(report);

        MvcResult result = mockMvc.perform(post("/general_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Double> basicStats = (Map<String, Double>) response.get("basicStats");
        assertThat(basicStats.get("totalIncome")).isEqualTo(800.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Успешная генерация отчета с указанием только end")
    public void testGenerateReportWithOnlyEnd() throws Exception {
        String jsonRequest = "{\"start\":\"\",\"end\":\"2025-04-02T10:00\"}";
        TransactionStatsService.FinancialReport report = new TransactionStatsService.FinancialReport(
                600.0, 200.0, 400.0, new HashMap<>(), new double[]{800.0, 150.0, 250.0, 350.0, 450.0}
        );

        when(transactionStatsService.generateGeneralReport(any(User.class), isNull(), any(LocalDateTime.class))).thenReturn(report);

        MvcResult result = mockMvc.perform(post("/general_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Double> basicStats = (Map<String, Double>) response.get("basicStats");
        assertThat(basicStats.get("totalBalance")).isEqualTo(400.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Успешная генерация отчета без указания дат")
    public void testGenerateReportWithoutDates() throws Exception {
        String jsonRequest = "{\"start\":\"\",\"end\":\"\"}";
        TransactionStatsService.FinancialReport report = new TransactionStatsService.FinancialReport(
                500.0, 100.0, 400.0, new HashMap<>(), new double[]{700.0, 120.0, 220.0, 320.0, 420.0}
        );

        when(transactionStatsService.generateGeneralReport(any(User.class), isNull(), isNull())).thenReturn(report);

        MvcResult result = mockMvc.perform(post("/general_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Double> goalData = (Map<String, Double>) response.get("goalData");
        assertThat(goalData.get("saved")).isEqualTo(320.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Генерация отчета при отсутствии транзакций")
    public void testGenerateReportNoTransactions() throws Exception {
        String jsonRequest = "{\"start\":\"2025-04-01T10:00\",\"end\":\"2025-04-02T10:00\"}";

        when(transactionStatsService.generateGeneralReport(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);

        MvcResult result = mockMvc.perform(post("/general_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Double> basicStats = (Map<String, Double>) response.get("basicStats");
        Map<String, Double> categoryReport = (Map<String, Double>) response.get("categoryReport");
        Map<String, Double> goalData = (Map<String, Double>) response.get("goalData");

        assertThat(basicStats.get("totalIncome")).isEqualTo(0.0);
        assertThat(basicStats.get("totalExpense")).isEqualTo(0.0);
        assertThat(basicStats.get("totalBalance")).isEqualTo(0.0);
        assertThat(categoryReport).isEmpty();
        assertThat(goalData.get("goalSum")).isEqualTo(0.0);
        assertThat(goalData.get("goalIncome")).isEqualTo(0.0);
        assertThat(goalData.get("goalExpense")).isEqualTo(0.0);
        assertThat(goalData.get("saved")).isEqualTo(0.0);
        assertThat(goalData.get("left")).isEqualTo(0.0);
    }
}
