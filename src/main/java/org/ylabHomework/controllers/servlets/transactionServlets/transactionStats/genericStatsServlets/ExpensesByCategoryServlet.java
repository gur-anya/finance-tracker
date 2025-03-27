package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.genericStatsServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExpensesByCategoryServlet", urlPatterns = "/summary_expenses_by_categories")
public class ExpensesByCategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("transaction_stats_jsps/summary_expenses_by_categories_page.jsp").forward(req, resp);
    }
}
