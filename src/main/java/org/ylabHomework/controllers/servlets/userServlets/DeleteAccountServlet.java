package org.ylabHomework.controllers.servlets.userServlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.ylabHomework.serviceClasses.Constants.DELETE_ACCOUNT_JSP;
/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может удалить свой аккаунт.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "DeleteAccountServlet", urlPatterns = "/delete_account")
public class DeleteAccountServlet extends HttpServlet {

    UserService userService;

    @Override
    public void init() {
        this.userService = new UserService(
                (UserRepository) getServletContext().getAttribute("userRepository"));
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(DELETE_ACCOUNT_JSP).forward(req, resp);
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String email = session.getAttribute("useremail").toString();
        userService.deleteUserByEmail(email);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.sendRedirect("/logout");
    }
    private String parseJsonResponse(HttpServletResponse resp) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при удалении пользователя! Попробуйте еще раз.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }
    @Override
    public void destroy() {
        this.userService = null;
    }


}
