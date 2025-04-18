package org.ylabHomework.controllers.userControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.mappers.UserMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

@Tag(name = "API регистрации")
@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(
            summary = "Показать страницу регистрации",
            description = "Перенаправляет на страницу регистрации нового пользователя")
    @ApiResponse(responseCode = "200", description = "Страница регистрации")
    @GetMapping(value = "/registration")
    public String showRegistrationPage() {
        return Constants.REGISTRATION_JSP;
    }

    @Operation(
            summary = "Зарегистрировать нового пользователя",
            description = "Создаёт нового пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация успешна: Успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные. Сообщение содержит список ошибок валидации"),
            @ApiResponse(responseCode = "409", description = "Конфликт: попытка зарегистрировать зарегистрированный email"),
    })
    @PostMapping(value = "/registration")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> registerUser(
            @Valid @RequestBody BasicUserDTO userDTO,
            BindingResult result) {
        StringBuilder stateMessageBuilder = new StringBuilder();
        String stateMessage;
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            if (stateMessage.contains("Пользователь с таким email уже зарегистрирован!")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessageDTO);
            } else {
                return ResponseEntity.badRequest().body(responseMessageDTO);
            }
        } else {
            stateMessageBuilder.append("Успешно!");
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            User newUser = this.userMapper.toModel(userDTO);
            this.userService.createUser(newUser.getName(), newUser.getEmail(), newUser.getPassword());
            return ResponseEntity.ok().body(responseMessageDTO);
        }
    }
}