package TransactionControllersTests.TransactionManagementTests;
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
import org.ylabHomework.DTOs.ResponseMessageDTO;

import org.ylabHomework.controllers.financeControllers.financeManagementControllers.FilterTransactionsController;
import org.ylabHomework.models.User;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.services.TransactionService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilterTransactionsTests {
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private FilterTransactionsController filterTransactionsController;
    private final MockHttpSession session = new MockHttpSession();
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("anya", "anya@ya.ru", "1234", 1);
        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getName());
        session.setAttribute("useremail", user.getEmail());
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(filterTransactionsController)
                .build();
    }


    @Test
    @DisplayName("Успешная фильтрация транзакций до даты")
    public void testFilterTransactionsBeforeDateSuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"1\",\"beforeDate\":\"2025-04-01T10:00\"}";
        List<Transaction> transactions = List.of(new Transaction(1, 100.0, "еда", "перекус"));

        when(transactionService.getTransactionsBeforeTimestamp(any(User.class), any(LocalDateTime.class))).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация до даты без транзакций")
    public void testFilterTransactionsBeforeDateEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"1\",\"beforeDate\":\"2025-04-01T10:00\"}";

        when(transactionService.getTransactionsBeforeTimestamp(any(User.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Транзакции до указанной даты не найдены!");
    }

    @Test
    @DisplayName("Фильтрация до даты без указания даты")
    public void testFilterTransactionsBeforeDateNoDate() throws Exception {
        String jsonRequest = "{\"filter\":\"1\"}";

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Укажите дату для фильтрации!");
    }

    @Test
    @DisplayName("Успешная фильтрация транзакций после даты")
    public void testFilterTransactionsAfterDateSuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"2\",\"afterDate\":\"2025-04-01T10:00\"}";
        List<Transaction> transactions = List.of(new Transaction(1, 200.0, "кофе", "вкусный"));

        when(transactionService.getTransactionsAfterTimestamp(any(User.class), any(LocalDateTime.class))).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация после даты без транзакций")
    public void testFilterTransactionsAfterDateEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"2\",\"afterDate\":\"2025-04-01T10:00\"}";

        when(transactionService.getTransactionsAfterTimestamp(any(User.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Транзакции после указанной даты не найдены!");
    }

    @Test
    @DisplayName("Фильтрация после даты без указания даты")
    public void testFilterTransactionsAfterDateNoDate() throws Exception {
        String jsonRequest = "{\"filter\":\"2\"}";

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Укажите дату для фильтрации!");
    }

    @Test
    @DisplayName("Успешная фильтрация по категории")
    public void testFilterTransactionsByCategorySuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"3\",\"category\":\"еда\"}";
        List<Transaction> transactions = List.of(new Transaction(1, 100.0, "еда", "перекус"));

        when(transactionService.getTransactionsByCategory(user,"еда")).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация по категории без транзакций")
    public void testFilterTransactionsByCategoryEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"3\",\"category\":\"еда\"}";

        when(transactionService.getTransactionsByCategory(user, "еда")).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Транзакции по категории 'еда' не найдены!");
    }

    @Test
    @DisplayName("Фильтрация по пустой категории")
    public void testFilterTransactionsByEmptyCategory() throws Exception {
        String jsonRequest = "{\"filter\":\"3\",\"category\":\"\"}";

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Укажите категорию для фильтрации!");
    }

    @Test
    @DisplayName("Успешная фильтрация доходов")
    public void testFilterTransactionsByIncomeSuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"41\"}";
        List<Transaction> transactions = List.of(new Transaction(1, 100.0, "нашла на улице", "класс"));

        when(transactionService.getTransactionsByType(user,1)).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация доходов без транзакций")
    public void testFilterTransactionsByIncomeEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"41\"}";

        when(transactionService.getTransactionsByType(user,1)).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Доходы не найдены!");
    }

    @Test
    @DisplayName("Успешная фильтрация расходов")
    public void testFilterTransactionsByExpenseSuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"42\"}";
        List<Transaction> transactions = List.of(new Transaction(2, 50.0, "еда", "перекус"));

        when(transactionService.getTransactionsByType(user,2)).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация расходов без транзакций")
    public void testFilterTransactionsByExpenseEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"42\"}";

        when(transactionService.getTransactionsByType(user,2)).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Расходы не найдены!");
    }

    @Test
    @DisplayName("Успешная фильтрация всех транзакций")
    public void testFilterAllTransactionsSuccess() throws Exception {
        String jsonRequest = "{\"filter\":\"5\"}";
        List<Transaction> transactions = List.of(new Transaction(1, 100.0, "еда", "перекус"));

        when(transactionService.getAllTransactions(user)).thenReturn(transactions);
        when(transactionMapper.toDTOList(transactions)).thenReturn(List.of(new BasicTransactionDTO()));

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("");
    }

    @Test
    @DisplayName("Фильтрация всех транзакций при их отсутствии")
    public void testFilterAllTransactionsEmpty() throws Exception {
        String jsonRequest = "{\"filter\":\"5\"}";

        when(transactionService.getAllTransactions(user)).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Транзакции отсутствуют!");
    }

    @Test
    @DisplayName("Фильтрация по неверному фильтру")
    public void testFilterTransactionsInvalidFilter() throws Exception {
        String jsonRequest = "{\"filter\":\"invalid\"}";

        MvcResult result = mockMvc.perform(post("/show_transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest)
                        .session(session))
                .andExpect(status().isBadRequest())
                .andReturn();

      String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResponseMessageDTO response = new ResponseMessageDTO(responseContent);
        assertThat(response.getMessage()).contains("Неверный фильтр!");
    }
}