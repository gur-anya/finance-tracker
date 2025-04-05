package org.ylabHomework.controllers.userControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.mappers.UserMappers.LoginMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Api(value = "API авторизации: логин, логаут")
@Controller
@RequiredArgsConstructor
public class AuthorizationController {
    private final LoginMapper loginMapper;

    private final UserService userService;

    @ApiOperation(value = "Показать страницу логина",
            notes = "Перенаправляет на страницу входа в систему")
    @ApiResponse(code = 200, message = "Страница логина")
    @GetMapping(value = "/login")
    public String showLoginPage() {
        return Constants.LOGIN_JSP;
    }

    @ApiOperation(value = "Вход пользователя в систему",
            notes = "Аутентифицирует пользователя и создаёт сессию. В случае ошибки валидации или неверных данных возвращает сообщение.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный вход: Успешно!", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Некорректные данные. Сообщение содержит список ошибок валидации",
                    response = ResponseMessageDTO.class),
            @ApiResponse(code = 401, message = "Ошибка аутентификации: Произошла ошибка! Попробуйте еще раз!",
                    response = ResponseMessageDTO.class)
    })
    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> loginUser(
            @Valid @RequestBody LoginDTO loginDTO,
            BindingResult result,
            HttpSession session) {
        StringBuilder stateMessageBuilder = new StringBuilder();
        stateMessageBuilder.append("Успешно!");
        String stateMessage;
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();

        if (result.hasErrors()) {
            stateMessageBuilder.delete(0, 7);
            for (ObjectError error : result.getAllErrors()) {
                stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.badRequest().body(responseMessageDTO);
        } else {
            User loginUser = loginMapper.toModel(loginDTO);
            UserService.LoginResult loginResult = userService.loginUser(loginUser.getEmail(), loginUser.getPassword());

            if (loginResult.success()) {
                session.setAttribute("loggedUser", loginResult.user());
                session.setAttribute("username", loginResult.user().getName());
                session.setAttribute("useremail", loginUser.getEmail());
                stateMessage = stateMessageBuilder.toString();
                responseMessageDTO.setMessage(stateMessage);
                return ResponseEntity.ok().body(responseMessageDTO);
            }
        }
        stateMessage = "Произошла ошибка! Попробуйте еще раз!";
        responseMessageDTO.setMessage(stateMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessageDTO);
    }

    @ApiOperation(value = "Показать главную страницу пользователя",
            notes = "Перенаправляет на главную страницу пользователя")
    @ApiResponse(code = 200, message = "Главная страница пользователя")
    @GetMapping(value = "/main_user_page")
    public String showUserMainPage() {
        return Constants.USER_MAIN_JSP;
    }

    @ApiOperation(value = "Выход пользователя из системы",
            notes = "Завершает сессию пользователя и перенаправляет на главную страницу для неавторизованных пользователей")
    @ApiResponse(code = 200, message = "Успешный выход и перенаправление")
    @GetMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedUser");
        session.removeAttribute("username");
        session.removeAttribute("useremail");
        session.removeAttribute("transactionRepository");
        session.invalidate();
        return "redirect:/";
    }
}
