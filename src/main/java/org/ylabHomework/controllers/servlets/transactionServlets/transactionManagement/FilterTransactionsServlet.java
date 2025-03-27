package org.ylabHomework.controllers.servlets.transactionServlets.transactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.mappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.services.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ylabHomework.serviceClasses.Constants.SHOW_TRANSACTIONS_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может просмотреть транзакции с заданным фильтром.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "FilterTransactionsServlet", urlPatterns = "/show_transactions")
public class FilterTransactionsServlet extends HttpServlet {
    TransactionService transactionService;
    TransactionMapper transactionMapper;

    @Override
    public void init() throws ServletException {
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionService = new TransactionService(transRepo, user);
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(SHOW_TRANSACTIONS_JSP).forward(req, resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonData = mapper.readValue(request.getReader(), Map.class);
        String filter = jsonData.get("filter");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        List<Transaction> transactions = new ArrayList<>();
        String stateMessage = "";
        switch (filter) {
            case "1":
                String beforeDateStr = jsonData.get("beforeDate");
                if (beforeDateStr != null && !beforeDateStr.isEmpty()) {
                    LocalDateTime beforeDate = LocalDateTime.parse(beforeDateStr, formatter);
                    transactions = transactionService.getTransactionsBeforeTimestamp(beforeDate);
                    if (transactions.isEmpty()) {
                        stateMessage = "Транзакции до указанной даты не найдены!";
                    }
                } else {
                    stateMessage = "Укажите дату для фильтрации!";
                }
                break;
            case "2":
                String afterDateStr = jsonData.get("afterDate");
                if (afterDateStr != null && !afterDateStr.isEmpty()) {
                    LocalDateTime afterDate = LocalDateTime.parse(afterDateStr, formatter);
                    transactions = transactionService.getTransactionsAfterTimestamp(afterDate);
                    if (transactions.isEmpty()) {
                        stateMessage = "Транзакции после указанной даты не найдены!";
                    }
                } else {
                    stateMessage = "Укажите дату для фильтрации!";
                }
                break;
            case "3":
                String category = jsonData.get("category");
                if (category != null && !category.isEmpty()) {
                    transactions = transactionService.getTransactionsByCategory(category);
                    if (transactions.isEmpty()) {
                        stateMessage = "Транзакции по категории '" + category + "' не найдены!";
                    }
                } else {
                    stateMessage = "Укажите категорию для фильтрации!";
                }
                break;
            case "41":
                transactions = transactionService.getTransactionsByType(1);
                if (transactions.isEmpty()) {
                    stateMessage = "Доходы не найдены!";
                }
                break;
            case "42":
                transactions = transactionService.getTransactionsByType(2);
                if (transactions.isEmpty()) {
                    stateMessage = "Расходы не найдены !";
                }
                break;
            case "5":
                transactions = transactionService.getAllTransactions();
                if (transactions.isEmpty()) {
                    stateMessage = "Транзакции отсутствуют!";
                }
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                stateMessage = "Неверный фильтр!";
        }


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("transactions", transactionMapper.toDTOList(transactions));
        responseData.put("message", stateMessage);
        String jsonResponse = mapper.writeValueAsString(responseData);

        response.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
        this.transactionService = null;
        this.transactionMapper = null;
    }
}
