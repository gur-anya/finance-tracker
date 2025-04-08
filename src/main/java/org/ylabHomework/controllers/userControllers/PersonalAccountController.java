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
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Api(value = "API личного кабинета")
@Controller
@RequiredArgsConstructor
public class PersonalAccountController {
    private final UserService userService;

    @ApiOperation(value = "Показать страницу личного кабинета",
            notes = "Перенаправляет на страницу личного кабинета")
    @ApiResponse(code = 200, message = "Страница личного кабинета")
    @GetMapping(value = "/personal_account")
    public String showAccountPage() {
        return Constants.PERSONAL_ACCOUNT_JSP;
    }

    @ApiOperation(value = "Показать страницу удаления аккаунта",
            notes = "Перенаправляет на страницу удаления аккаунта")
    @ApiResponse(code = 200, message = "Страница удаления аккаунта")
    @GetMapping(value = "/delete_account")
    public String showDeleteAccountPage() {
        return Constants.DELETE_ACCOUNT_JSP;
    }

    @ApiOperation(value = "Показать страницу обновления аккаунта",
            notes = "Перенаправляет на страницу обновления данных аккаунта")
    @ApiResponse(code = 200, message = "Страница обновления аккаунта")
    @GetMapping(value = "/update_account")
    public String showUpdateAccountPage() {
        return Constants.UPDATE_ACCOUNT_JSP;
    }

    @ApiOperation(value = "Удалить аккаунт пользователя",
            notes = "Удаляет аккаунт по email из сессии и завершает сессию")
    @ApiResponse(code = 200, message = "Успешное удаление и перенаправление")
    @DeleteMapping(value = "/delete_account")
    @ResponseBody
    public String deleteAccount(HttpSession session) {
        String email = session.getAttribute("useremail").toString().trim().toLowerCase();
        userService.deleteUserByEmail(email);
        session.removeAttribute("loggedUser");
        session.removeAttribute("username");
        session.removeAttribute("useremail");
        session.removeAttribute("transactionRepository");
        session.invalidate();
        return "redirect:/";
    }

    @ApiOperation(value = "Обновить данные аккаунта пользователя",
            notes = "Обновляет имя, email или пароль пользователя. В случае ошибки валидации или конфликта возвращает сообщение.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные обновлены успешно", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Некорректные данные. Сообщение содержит список ошибок валидации",
                    response = ResponseMessageDTO.class),
            @ApiResponse(code = 409, message = "Конфликт данных (например, email уже занят)", response = ResponseMessageDTO.class)
    })
    @PostMapping(value = "/update_account")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> updateAccount(
            @Valid @RequestBody ActionsWithUserDTO actionsDTO,
            BindingResult result,
            HttpSession session) {
        StringBuilder stateMessageBuilder = new StringBuilder();
        String stateMessage = "";
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        boolean correctFlag = true;

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.badRequest().body(responseMessageDTO);
        } else {
            User user = (User) session.getAttribute("loggedUser");
            String email = user.getEmail().trim().toLowerCase();
            String updatedValues = actionsDTO.getUpdatedValues();

            if (updatedValues.contains("name")) {
                userService.updateName(actionsDTO.getName(), email);
                session.setAttribute("username", user.getName());
                stateMessageBuilder.append(actionsDTO.getName());
                stateMessageBuilder.append(", имя изменено успешно!");
                stateMessage = stateMessageBuilder.toString();
            }
            if (updatedValues.contains("email")) {
                String emailCheck = actionsDTO.getEmail();
                if (userService.emailCheck(emailCheck).contains("Пользователь с таким email уже зарегистрирован!")) {
                    stateMessageBuilder.append(" ");
                    stateMessageBuilder.append(userService.emailCheck(emailCheck));
                    stateMessage = stateMessageBuilder.toString();
                    correctFlag = false;
                } else {
                    stateMessageBuilder.append(" ");
                    stateMessageBuilder.append(userService.updateEmail(actionsDTO.getEmail(), email));
                    stateMessage = stateMessageBuilder.toString();
                    session.setAttribute("useremail", user.getEmail());
                }
            }
            if (updatedValues.contains("password")) {
                String passwordCheck = userService.updatePassword(actionsDTO.getNewPassword(), email);
                if (!passwordCheck.contains("Пароль успешно обновлён!")) {
                    stateMessageBuilder.append(" ");
                    stateMessageBuilder.append(passwordCheck);
                    stateMessage = stateMessageBuilder.toString();
                    correctFlag = false;
                } else {
                    stateMessageBuilder.append(" ");
                    stateMessageBuilder.append(passwordCheck);
                    stateMessage = stateMessageBuilder.toString();
                }
            }
        }
        responseMessageDTO.setMessage(stateMessage);
        if (!correctFlag) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessageDTO);
        } else {
            return ResponseEntity.ok().body(responseMessageDTO);
        }
    }
}
