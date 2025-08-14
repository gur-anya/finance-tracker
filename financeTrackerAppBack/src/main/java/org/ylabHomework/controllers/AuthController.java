package org.ylabHomework.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.ErrorResponse;
import org.ylabHomework.DTOs.MessageResponseDTO;
import org.ylabHomework.DTOs.userDTOs.CreateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.CreateUserResponseDTO;
import org.ylabHomework.DTOs.userDTOs.LoginRequestDTO;
import org.ylabHomework.DTOs.userDTOs.LoginResponseDTO;
import org.ylabHomework.serviceClasses.security.JWTCore;
import org.ylabHomework.serviceClasses.security.UserDetailsImpl;
import org.ylabHomework.services.TokenService;
import org.ylabHomework.services.UserService;

import java.time.LocalDateTime;

@RestController
@Tag(name = "API для работы с авторизацией: регистрация, вход и выход из системы")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final JWTCore jwtCore;

    @Operation(
        summary = "Регистрирует нового пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь создан - успешный ответ с данными пользователя"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные для регистрации"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponseDTO> signup(@RequestBody @Valid CreateUserRequestDTO dto) {
        CreateUserResponseDTO userResponseDTO = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @Operation(
        summary = "Производит вход пользователя в систему")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь вошел - успешный ответ с его JWT-токеном"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные для входа"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        Authentication authentication;
        authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        String jwt = jwtCore.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Long userId = userDetails.getId();

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(userId, jwt);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @Operation(
        summary = "Производит выход пользователя из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно вышел из системы"),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос для выхода из системы"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            tokenService.blacklistToken(jwt);

            return ResponseEntity.ok(new MessageResponseDTO("Logout successful"));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Incorrect logout request", LocalDateTime.now()));
    }
}