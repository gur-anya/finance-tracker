package org.ylabHomework.controllers.servlets.userServlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.mappers.UserMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


import static org.ylabHomework.serviceClasses.Constants.REGISTRATION_JSP;
/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может зарегистрироваться.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "RegistrationServlet", urlPatterns = "/registration")
public class RegistrationServlet extends HttpServlet {

    UserService userService;
    UserMapper userMapper;

    @Override
    public void init() {
        this.userService = new UserService(
                (UserRepository) getServletContext().getAttribute("userRepository"));

        this.userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(REGISTRATION_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BasicUserDTO userDTO = (BasicUserDTO) req.getAttribute("DTO");
        req.removeAttribute("DTO");
        User newUser = this.userMapper.toModel(userDTO);
        this.userService.createUser(newUser.getName(), newUser.getEmail(), newUser.getPassword());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(parseJsonResponse(resp));

    }

    @Override
    public void destroy() {
        this.userService = null;
    }

    private String parseJsonResponse(HttpServletResponse resp) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при регистрации пользователя! Попробуйте еще раз.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }

}