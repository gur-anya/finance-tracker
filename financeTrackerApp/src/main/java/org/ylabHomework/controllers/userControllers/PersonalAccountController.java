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
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

@Tag(name = "API личного кабинета")
@Controller
@RequiredArgsConstructor
public class PersonalAccountController {
    private final UserService userService;

    @Operation(
            summary = "Показать страницу личного кабинета",
            description = "Перенаправляет на страницу личного кабинета")
    @ApiResponse(responseCode = "200", description = "Страница личного кабинета")
    @GetMapping(value = "/personal_account")
    public String showAccountPage() {
        return Constants.PERSONAL_ACCOUNT_JSP;
    }

    @Operation(
            summary = "Показать страницу удаления аккаунта",
            description = "Перенаправляет на страницу удаления аккаунта")
    @ApiResponse(responseCode = "200", description = "Страница удаления аккаунта")
    @GetMapping(value = "/delete_account")
    public String showDeleteAccountPage() {
        return Constants.DELETE_ACCOUNT_JSP;
    }

    @Operation(
            summary = "Показать страницу обновления аккаунта",
            description = "Перенаправляет на страницу обновления данных аккаунта")
    @ApiResponse(responseCode = "200", description = "Страница обновления аккаунта")
    @GetMapping(value = "/update_account")
    public String showUpdateAccountPage() {
        return Constants.UPDATE_ACCOUNT_JSP;
    }

    @Operation(
            summary = "Удалить аккаунт пользователя",
            description = "Удаляет аккаунт по email из сессии и завершает сессию")
    @ApiResponse(responseCode = "200", description = "Успешное удаление и перенаправление")
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

    @Operation(
            summary = "Обновить данные аккаунта пользователя",
            description = "Обновляет имя, email или пароль пользователя. В случае ошибки валидации или конфликта возвращает сообщение.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные. Сообщение содержит список ошибок валидации"),
            @ApiResponse(responseCode = "409", description = "Конфликт данных (например, email уже занят)")
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
