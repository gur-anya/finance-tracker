package org.ylabHomework.controllers.servlets.userServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.PERSONAL_ACCOUNT_JSP;
/**
 * Сервлет, демонстрирующий пользователю страницу с его личным кабинетом,
 * где он может перейти к настройкам аккаунта или к его удалению.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "PersonalAccountServlet", urlPatterns = "/personal_account")
public class PersonalAccountServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        req.setAttribute("username", session.getAttribute("username"));
        req.getRequestDispatcher(PERSONAL_ACCOUNT_JSP).forward(req, resp);
    }
}
