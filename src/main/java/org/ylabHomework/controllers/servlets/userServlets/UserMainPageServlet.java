package org.ylabHomework.controllers.servlets.userServlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


import static org.ylabHomework.serviceClasses.Constants.USER_MAIN_JSP;

/**
 * Сервлет, демонстрирующий пользователю страницу с главным меню, с которой
 * он может перейти к настройкам аккаунта или к управлению финансами.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "UserMainPageServlet", urlPatterns = "/main_user_page")
public class UserMainPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        req.setAttribute("username", session.getAttribute("username"));
        req.getRequestDispatcher(USER_MAIN_JSP).forward(req, resp);
    }
}
