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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.ylabHomework.serviceClasses.Constants.GENERAL_REPORT_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может просмотреть общий финансовый отчет.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "GeneralReportServlet", urlPatterns = "/general_report")
public class GeneralReportServlet extends HttpServlet {
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
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(GENERAL_REPORT_JSP).forward(req, resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonData = mapper.readValue(req.getReader(), Map.class);
        String start = jsonData.get("start");
        String end = jsonData.get("end");

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        if (!start.isEmpty()) {
            startTime = LocalDateTime.parse(start, formatter);
        }
        if (!end.isEmpty()) {
            endTime = LocalDateTime.parse(end, formatter);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, Object> responseData = new HashMap<>();
        TransactionStatsService.FinancialReport report = transactionStatsService.generateGeneralReport(startTime, endTime);

        Map<String, Double> basicStats = new HashMap<>();
        basicStats.put("totalIncome", report.totalIncome);
        basicStats.put("totalExpense", report.totalExpense);
        basicStats.put("totalBalance", report.totalBalance);

        responseData.put("basicStats", basicStats);
        responseData.put("categoryReport", report.categoryReport);

        Map<String, Double> goalData = new HashMap<>();
        goalData.put("goalSum", report.goalData[0]);
        goalData.put("goalIncome", report.goalData[1]);
        goalData.put("goalExpense", report.goalData[2]);
        goalData.put("saved", report.goalData[3]);
        goalData.put("left", report.goalData[4]);

        responseData.put("goalData", goalData);

        String jsonResponse = mapper.writeValueAsString(responseData);


        resp.getWriter().write(jsonResponse);
    }

    @Override
    public void destroy() {
        this.transactionStatsService = null;
        this.transactionMapper = null;
    }
}
