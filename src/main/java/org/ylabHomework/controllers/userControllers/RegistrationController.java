package org.ylabHomework.controllers.userControllers;


import javax.validation.*;

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
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.mappers.UserMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.UserService;

@Api(value = "API регистрации")
@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserMapper userMapper;
    private final UserService userService;
    @ApiOperation(value = "Показать страницу регистрации",
            notes = "Перенаправляет на страницу регистрации нового пользователя")
    @ApiResponse(code = 200, message = "Страница регистрации")
    @GetMapping(value = "/registration")
    public String showRegistrationPage() {
        return Constants.REGISTRATION_JSP;
    }

    @ApiOperation(value = "Зарегистрировать нового пользователя",
            notes = "Создаёт нового пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Регистрация успешна: Успешно!", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Некорректные данные. Сообщение содержит список ошибок валидации",
                    response = ResponseMessageDTO.class),
            @ApiResponse(code = 409, message = "Конфликт: попытка зарегистрировать зарегистрированный email",
                    response = ResponseMessageDTO.class),
    })
    @PostMapping(value = "/registration")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> registerUser(
            @Valid @RequestBody BasicUserDTO userDTO,
            BindingResult result){
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