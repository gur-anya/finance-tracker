package org.ylabHomework.controllers.financeControllers.financeStatsControllers.userControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.UpdateUserDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TokenService;
import org.ylabHomework.services.UserService;

@Tag(name="API управления профилем: редактирование, удаление профиля, выход из аккаунта")
@RestController
@RequiredArgsConstructor
public class ManagementController {
    private final UserService userService;
    private final TokenService tokenService;


    @Operation(
            summary = "Редактировать данные аккаунта пользователя",
            description = "Обновляет имя, email или пароль пользователя. В случае ошибки валидации или конфликта возвращает сообщение.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные. Сообщение содержит список ошибок"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "409", description = "Конфликт данных (например, email уже занят)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/profile")

    public ResponseEntity<ResponseMessageDTO> updateAccount(@RequestBody @Valid UpdateUserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        StringBuilder message = new StringBuilder();
        message.append("Успешное обновление ");
        if (user != null) {
            if (!dto.isUpdateEmail() && !dto.isUpdateName() && !dto.isUpdatePassword()) {
                return ResponseEntity.badRequest().body(new ResponseMessageDTO("Вы не сделали ни одного изменения!"));
            }

            if (dto.isUpdateEmail()) {
                userService.updateUserEmail(dto.getEmail(), user);
                message.append("email");
            }

            if (dto.isUpdateName()) {
                userService.updateUserName(dto.getName(), user);
                if (message.toString().contains("email")) {
                    message.append(", имени");
                } else {
                    message.append(" имени");
                }
            }

            if (dto.isUpdatePassword()) {
                userService.updateUserPassword(dto.getPassword(), dto.getOldPassword(), user);
                if (message.toString().contains("email") || message.toString().contains("имени")) {
                    message.append(", пароля");
                } else {
                    message.append(" пароля");
                }
            }

            message.append("!");
            return ResponseEntity.ok().body(new ResponseMessageDTO(message.toString()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }

    @Operation(
            summary = "Удалить аккаунт пользователя",
            description = "Удаляет аккаунт и инвалдирует токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление и инвалидация"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/profile")

    public ResponseEntity<ResponseMessageDTO> deleteAccount(@RequestHeader("Authorization") String bearerToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            if (!userService.deleteUser(user)) {
                return ResponseEntity.internalServerError()
                        .body(new ResponseMessageDTO("Произошла ошибка при удалении пользователя!"));
            } else {
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);
                    tokenService.blacklistToken(token);
                } else {
                    return ResponseEntity.internalServerError()
                            .body(new ResponseMessageDTO("Произошла ошибка при выходе из удаленного аккаунта!"));
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
        return ResponseEntity.internalServerError()
                .body(new ResponseMessageDTO("Произошла ошибка!"));
    }

    @Operation(
            summary = "Выход пользователя из системы",
            description = "Инвалидирует токен пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/logout")
    @PostMapping("/logout")

    public ResponseEntity<ResponseMessageDTO> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(new ResponseMessageDTO("Неуспешный выход из аккаунта: токен отсутствует или имеет неверный формат!"));
            }
            String token = bearerToken.substring(7);
            tokenService.blacklistToken(token);
            return ResponseEntity.ok(new ResponseMessageDTO("Успешно!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseMessageDTO(e.getMessage()));
        }
    }
}
