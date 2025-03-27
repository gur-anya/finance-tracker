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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вспомогательный сервлет, передающий все транзакции.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "GetAllTransactionsServlet", urlPatterns = "/get_all")
public class GetAllTransactionsServlet extends HttpServlet {
    TransactionService transactionService;
    TransactionMapper transactionMapper;

    @Override
    public void init(){
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionService = new TransactionService(transRepo, user);
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> responseData = new HashMap<>();
        List<Transaction> transactions = transactionService.getAllTransactions();
        responseData.put("transactions", transactionMapper.toDTOList(transactions));

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(responseData);

        resp.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
        this.transactionService = null;
        this.transactionMapper = null;
    }

}
