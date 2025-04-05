package org.ylabHomework.controllers.userControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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

@Tag(name = "API авторизации: логин, логаут")
@Controller
@RequiredArgsConstructor
public class AuthorizationController {
    private final LoginMapper loginMapper;

    private final UserService userService;

    @Operation(
            summary = "Показать страницу логина",
            description = "Перенаправляет на страницу входа в систему")
    @ApiResponse(responseCode = "200", description = "Страница логина")
    @GetMapping(value = "/login")
    public String showLoginPage() {
        return Constants.LOGIN_JSP;
    }

    @Operation(
            summary = "Вход пользователя в систему",
            description = "Аутентифицирует пользователя и создаёт сессию. В случае ошибки валидации или неверных данных возвращает сообщение.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход: Успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные. Сообщение содержит список ошибок валидации"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации: Произошла ошибка! Попробуйте еще раз!")
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

    @Operation(
            summary = "Показать главную страницу пользователя",
            description = "Перенаправляет на главную страницу пользователя")
    @ApiResponse(responseCode = "200", description = "Главная страница пользователя")
    @GetMapping(value = "/main_user_page")
    public String showUserMainPage() {
        return Constants.USER_MAIN_JSP;
    }

    @Operation(
            summary = "Выход пользователя из системы",
            description = "Завершает сессию пользователя и перенаправляет на главную страницу для неавторизованных пользователей")
    @ApiResponse(responseCode = "200", description = "Успешный выход и перенаправление")
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
