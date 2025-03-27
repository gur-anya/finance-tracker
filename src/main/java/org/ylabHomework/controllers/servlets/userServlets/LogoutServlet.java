package org.ylabHomework.controllers.servlets.userServlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Сервлет, инициирующий выход из аккаунта.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        getServletContext().setAttribute("user", null);
        getServletContext().setAttribute("transactionRepository", null);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect("/");
    }
}
