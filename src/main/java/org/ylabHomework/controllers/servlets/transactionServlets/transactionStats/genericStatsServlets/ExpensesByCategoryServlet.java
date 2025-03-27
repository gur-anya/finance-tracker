package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.genericStatsServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.CATEGORY_EXPENSES_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может просмотреть расходы по категориям.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */

@WebServlet(name = "ExpensesByCategoryServlet", urlPatterns = "/summary_expenses_by_categories")
public class ExpensesByCategoryServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(CATEGORY_EXPENSES_JSP).forward(req, resp);
    }
}
