package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.genericStatsServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.mappers.TransactionMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.services.TransactionStatsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Вспомогательный сервлет, передающий данные о балансе.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */

@WebServlet(name = "GetBalanceServlet", urlPatterns = "/get_balance")
public class GetBalanceServlet extends HttpServlet {
    TransactionStatsService transactionStatsService;
    TransactionMapper transactionMapper;

    @Override
    public void init(){
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionStatsService = new TransactionStatsService(transRepo, user);
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, String> responseData = new HashMap<>();
        String balance = transactionStatsService.calculateBalance();
        responseData.put("balance", balance);

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(responseData);


        resp.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
        this.transactionStatsService = null;
        this.transactionMapper = null;
    }

}