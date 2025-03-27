package org.ylabHomework.controllers.servlets.transactionServlets.transactionManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.TRANSACTIONS_MANAGEMENT_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу с главным меню финансов, с которой
 * он может перейти к управлению финансами или к финансовой статистике.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@WebServlet(name = "TransactionManagementPageServlet", urlPatterns = "/transactions_management_page")
public class TransactionManagementPageServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        req.setAttribute("useremail", session.getAttribute("useremail"));
        req.setAttribute("username", session.getAttribute("username"));
        req.getRequestDispatcher(TRANSACTIONS_MANAGEMENT_JSP).forward(req, resp);
    }
}
