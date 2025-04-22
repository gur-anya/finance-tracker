package org.ylabHomework.controllers.financeControllers.financeStatsControllers.userControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.AuthDTO;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.CreateUserDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.mappers.UserMappers.UserMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.springConfigs.security.JWTCore;
import org.ylabHomework.services.UserService;

@Tag(name = "API регистрации и входа пользователя")
@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTCore jwtCore;


    @Operation(
            summary = "Зарегистрировать нового пользователя",
            description = "Создаёт нового пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Регистрация успешна: Успешно!"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные. Сообщение содержит список ошибок"),
            @ApiResponse(responseCode = "409", description = "Конфликт: попытка зарегистрировать зарегистрированный email"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/signup")

    public ResponseEntity<ResponseMessageDTO> signup(@RequestBody @Valid CreateUserDTO dto) {
        User newUser = this.userMapper.toModel(dto);
        this.userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessageDTO("Успешно!"));
    }


    @Operation(
            summary = "Войти в аккаунт пользователя",
            description = "Производит вход в аккаунт пользователя и выдает ему токен. В случае ошибки входа возвращает список ошибок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вход успешен"),
            @ApiResponse(responseCode = "403", description = "Некорректные данные для входа. Сообщение содержит список ошибок"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {
        if (userService.readUserByEmail(dto.getEmail()) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Пользователь с таким email не найден!"));
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDTO("Неверный пароль!"));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok().body(new AuthDTO(jwt));
    }
}
