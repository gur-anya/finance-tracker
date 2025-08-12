package org.ylabHomework.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.MessageResponseDTO;
import org.ylabHomework.DTOs.userDTOs.GetAllUsersResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserRequestDTO;
import org.ylabHomework.DTOs.userDTOs.UpdateUserResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UserDTO;
import org.ylabHomework.services.UserService;

@RestController
@Tag(name = "API для работы с пользователями: чтение всех пользователей, изменение и удаление, блокировка и разблокировка пользователя")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
        summary = "Обновляет данные пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление - ответ с обновленным пользователем"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные для обновления"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Пользователю запрещено совершать обновление"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO,
                                                            @PathVariable Long userId) {
        UpdateUserResponseDTO userResponseDTO = userService.updateUser(updateUserRequestDTO, userId);
        return ResponseEntity.ok(userResponseDTO);

    }

    @Operation(
        summary = "Удаляет пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Успешное удаление - пустой ответ"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные для обновления"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Пользователю запрещено совершать удаление (пользователь пытается удалить не себя, не являясь администратором)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
        summary = "Блокирует пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная блокировка"),
        @ApiResponse(responseCode = "400", description = "Невалидная попытка блокировки (блокировка заблокированного пользователя)"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Запрещено совершать блокировку (пользователь не является администратором)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/block/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> blockUser(@PathVariable Long userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok(new MessageResponseDTO("User " + userId + " blocked successfully."));

    }

    @Operation(
        summary = "Разблокирует пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная разблокировка"),
        @ApiResponse(responseCode = "400", description = "Невалидная попытка разблокировки (пользователь не заблокирован)"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Запрещено совершать разблокировку (пользователь не является администратором)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/unblock/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok(new MessageResponseDTO("User " + userId + " unblocked successfully."));
    }

    @Operation(
        summary = "Получает всех пользователей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ со списком всех пользователей"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Запрещено получать список пользователей (пользователь не является администратором)"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetAllUsersResponseDTO> getAllUsers() {
        GetAllUsersResponseDTO usersResponseDTO = userService.getAllUsers();
        return ResponseEntity.ok(usersResponseDTO);
    }

    @Operation(
        summary = "Получает пользователя по id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с данными пользователя"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "403", description = "Запрещено получить данные пользователя"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }
}
