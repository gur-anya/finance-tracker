package org.ylabHomework.controllers.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.INDEX_JSP;

/**
 * Сервлет, демонстрирующий пользователю приветственную страницу, где он может зарегистрироваться или войти в аккаунт.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "GreetingServlet", urlPatterns = "/")
public class GreetingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(INDEX_JSP).forward(req, resp);
    }
}
