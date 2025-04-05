package TransactionControllersTests.TransactionManagementTests;


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
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.Main;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class CreateTransactionTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionStatsService transactionStatsService;
    @MockBean
    private TransactionMapper transactionMapper;

    private final MockHttpSession session = new MockHttpSession();
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
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
        verify(transactionService).createTransaction(user, 1, "100.0", "еда", "перекус");
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