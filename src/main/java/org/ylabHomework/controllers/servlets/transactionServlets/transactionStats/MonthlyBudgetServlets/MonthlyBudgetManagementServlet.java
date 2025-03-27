package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.MonthlyBudgetServlets;

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

@WebServlet(name = "MonthlyBudgetManagementServlet", urlPatterns = "/get_monthly_budget_management")
public class MonthlyBudgetManagementServlet extends HttpServlet {
    TransactionStatsService transactionStatsService;
    TransactionMapper transactionMapper;

    @Override
    public void init() {
        this.transactionMapper = Mappers.getMapper(TransactionMapper.class);
        TransactionRepository transRepo = (TransactionRepository) getServletContext().getAttribute("transactionRepository");
        User user = (User) getServletContext().getAttribute("user");
        transRepo.setUser(user);
        this.transactionStatsService = new TransactionStatsService(transRepo, user);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Double> responseData = new HashMap<>();
        double budget = transactionStatsService.getMonthlyBudget();
        responseData.put("budget", budget);

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
