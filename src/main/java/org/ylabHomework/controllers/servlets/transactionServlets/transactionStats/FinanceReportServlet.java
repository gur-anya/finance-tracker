package org.ylabHomework.controllers.servlets.transactionServlets.transactionStats;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "FinanceReportServlet", urlPatterns = "/general_report")
public class FinanceReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("transaction_stats_jsps/general_report_page.jsp").forward(req, resp);
    }
}
