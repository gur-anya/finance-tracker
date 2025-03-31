package org.ylabHomework.controllers.servlets.userServlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.mappers.LoginMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.LOGIN_JSP;
/**
 * Сервлет, демонстрирующий пользователю страницу логина, на которой он может войти в аккаунт.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    UserService userService;
    LoginMapper loginMapper;

    @Override
    public void init() {
        this.userService = new UserService((UserRepository) getServletContext().getAttribute("userRepository"));
        this.loginMapper = Mappers.getMapper(LoginMapper.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(LOGIN_JSP).forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO loginDTO = (LoginDTO) req.getAttribute("DTO");
        User loginUser = this.loginMapper.toModel(loginDTO);
        req.removeAttribute("DTO");
        UserService.LoginResult loginResult = this.userService.loginUser(loginUser.getEmail(), loginUser.getPassword());
        boolean loggedIn = loginResult.success();
        if (loggedIn) {
            resp.setStatus(HttpServletResponse.SC_OK);
            HttpSession session = req.getSession(true);
            getServletContext().setAttribute("user", userService.readUserByEmail(loginUser.getEmail()));
            getServletContext().setAttribute("transactionRepository", new TransactionRepository(userService.readUserByEmail(loginUser.getEmail())));
            session.setAttribute("loggedUser", loginResult.user());
            session.setAttribute("username", loginResult.user().getName());
            session.setAttribute("useremail", loginUser.getEmail());
            resp.sendRedirect("/main_user_page");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(parseJsonResponse(resp));
        }

    }

    @Override
    public void destroy() {
        this.userService = null;
    }

    private String parseJsonResponse(HttpServletResponse resp) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_CONFLICT) ->
                    responseMessageDTO.setMessage("Введен неверный пароль! Попробуйте еще раз.");
            default ->
                    responseMessageDTO.setMessage("Произошла ошибка при регистрации пользователя! Попробуйте еще раз.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }
}

