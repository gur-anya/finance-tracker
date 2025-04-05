package TransactionControllersTests.TransactionManagementTests;

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
import org.ylabHomework.Main;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class UpdateTransactionTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionStatsService transactionStatsService;
    private MockHttpSession session;
    private User user;

    @BeforeEach
    public void setup() throws SQLException {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session = new MockHttpSession();
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
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
        ResponseMessageDTO response = new ObjectMapper().readValue(responseContent, ResponseMessageDTO.class);
        assertThat(response.getMessage(), containsString("Вы не сделали ни одного изменения!"));
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
        ResponseMessageDTO response = new ObjectMapper().readValue(responseContent, ResponseMessageDTO.class);
        assertThat(response.getMessage(), containsString("Успешно!"));
        verify(transactionService).updateTransactionCategory(any(User.class), eq("кофе"), any(Transaction.class));
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
        ResponseMessageDTO response = new ObjectMapper().readValue(responseContent, ResponseMessageDTO.class);
        assertThat(response.getMessage(), containsString("Успешно! Превышен лимит на 500.0!"));
        verify(transactionService).updateTransactionType(any(User.class), eq(2), any(Transaction.class));
    }

    @Test
    @DisplayName("Неуспешное обновление с некорректными данными")
    public void testUpdateTransactionValidationFailure() throws Exception {
        String jsonRequest = "{\"originalType\":0,\"originalSum\":-10.0,\"originalCategory\":\"\",\"description\":\"\",\"updatedValues\":\"type\",\"type\":0,\"sum\":-10.0,\"category\":\"\"}";

        MvcResult result = mockMvc.perform(post("/update_transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ObjectMapper().readValue(responseContent, ResponseMessageDTO.class);
        assertThat(response.getMessage(), containsString("Тип должен быть равен 1 (доход) или 2 (расход)"));
        assertThat(response.getMessage(), containsString("Сумма должна быть больше нуля"));
        assertThat(response.getMessage(), containsString("Категория не должна быть пустой"));
    }
}