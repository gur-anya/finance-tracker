package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats.genericStatsServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "BalanceServlet", urlPatterns = "/current_balance")
public class BalanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("transaction_stats_jsps/balance_page.jsp").forward(req, resp);
    }
}

