package org.ylabHomework.controllers.servlets.userServlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.mappers.ActionWithUserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static org.ylabHomework.serviceClasses.Constants.UPDATE_ACCOUNT_JSP;
/**
 * Сервлет, демонстрирующий пользователю страницу, на которой он может
 * изменить личные данные (имя пользователя, email, пароль).
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@WebServlet(name = "UpdateAccountServlet", urlPatterns = "/update_account")
public class UpdateAccountServlet extends HttpServlet {

    UserService userService;
    ActionWithUserMapper actionWithUserMapper;

    @Override
    public void init() {
        this.userService = new UserService(
                (UserRepository) getServletContext().getAttribute("userRepository"));

        this.actionWithUserMapper = Mappers.getMapper(ActionWithUserMapper.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        req.setAttribute("username", session.getAttribute("username"));
        req.setAttribute("useremail", session.getAttribute("useremail"));
        req.getRequestDispatcher(UPDATE_ACCOUNT_JSP).forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ActionsWithUserDTO actionsDTO = (ActionsWithUserDTO) req.getAttribute("DTO");
        req.removeAttribute("DTO");

        HttpSession session = req.getSession(false);

        String email = session.getAttribute("useremail").toString();

        String json = req.getAttribute("json").toString();
        req.removeAttribute("json");

        User user = this.actionWithUserMapper.toModel(actionsDTO);

        String responseString = "";


        Map<String, String> paramAndValue = new ObjectMapper().readValue(json, Map.class);

        String updatedValues = paramAndValue.get("updatedValues");

        if (updatedValues.contains("name")) {
            userService.updateName(user.getName(), email);
            session.setAttribute("username", user.getName());
            resp.setStatus(HttpServletResponse.SC_OK);
            responseString += user.getName() + ", имя изменено успешно!";
        }
        if (updatedValues.contains("email")) {
            if (userService.emailCheck(user.getEmail()).equals("Пользователь с таким email уже зарегистрирован!")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                userService.updateEmail(user.getEmail(), email);
                session.setAttribute("useremail", user.getEmail());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            if (!responseString.contains(parseJsonResponseDTO(resp).getMessage())) {
                responseString += parseJsonResponseDTO(resp).getMessage();
            }
        }
        if (updatedValues.contains("password")) {
            userService.updatePassword(user.getPassword(), email);
            resp.setStatus(HttpServletResponse.SC_OK);
            if (!responseString.contains(parseJsonResponseDTO(resp).getMessage())) {
                responseString += parseJsonResponseDTO(resp).getMessage();
            }
        }
        if (!responseString.isEmpty()) {
            resp.getWriter().write(parseJsonResponse(responseString));
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
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при обновлении! Попробуйте еще раз.");
            case (HttpServletResponse.SC_CONFLICT) ->
                    responseMessageDTO.setMessage("Пользователь с таким email уже зарегистрирован, повторите попытку.");
        }
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }

    private String parseJsonResponse(String toJson) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO(toJson);
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }

    private ResponseMessageDTO parseJsonResponseDTO(HttpServletResponse resp) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        switch (resp.getStatus()) {
            case (HttpServletResponse.SC_OK) -> responseMessageDTO.setMessage("Успешно!");
            case (HttpServletResponse.SC_BAD_REQUEST) ->
                    responseMessageDTO.setMessage("Произошла ошибка при обновлении! Попробуйте еще раз.");
            case (HttpServletResponse.SC_CONFLICT) ->
                    responseMessageDTO.setMessage("Пользователь с таким email уже зарегистрирован, повторите попытку.");
        }
        return responseMessageDTO;
    }

}