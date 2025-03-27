package org.ylabHomework.controllers.servlets.transactionServlets;


import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.TRANSACTIONS_MAIN_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу с главным меню финансов, с которой
 * он может перейти к управлению финансами или к финансовой статистике.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "TransactionsMainPageServlet", urlPatterns = "/main_transaction_page")
public class TransactionsMainPageServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        TransactionRepository transactionRepository = new TransactionRepository((User) req.getAttribute("loggedUser"));
        getServletContext().setAttribute("transactionRepository", transactionRepository);
        req.getRequestDispatcher(TRANSACTIONS_MAIN_JSP).forward(req, resp);
    }
}

