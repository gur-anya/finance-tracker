package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.genericStatsServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.SHOW_STATS_PAGE_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может управлять статистикой по транзакциям.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "StatsManagementServlet", urlPatterns = "/general_stats_page")
public class StatsManagementServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(SHOW_STATS_PAGE_JSP).forward(req, resp);
    }
}

