package org.ylabHomework.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.CheckGoalResponseDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalRequestDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.GoalResponseDTO;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.UpdateGoalRequestDTO;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;
import org.ylabHomework.services.GoalService;

@RestController
@Tag(name = "API работы с финансовой целью пользователя: создание, получение, изменение, сброс цели и связанных с ней транказций, проверка накоплений")
@RequestMapping("/api/v1/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @Operation(
        summary = "Возвращает финансовую цель пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с целью пользователя"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> getUserGoal(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.getUserGoal(currentUser.getId());
        return ResponseEntity.ok(goalResponseDTO);
    }

    @Operation(
        summary = "Устанавливает и возращает финансовую цель пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с целью пользователя"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/set")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> setUserGoal(@RequestBody @Valid GoalRequestDTO goalRequestDTO,
                                                       @AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.setUserGoal(currentUser.getId(), goalRequestDTO);
        return ResponseEntity.ok(goalResponseDTO);
    }
    @Operation(
        summary = "Обновляет и возращает финансовую цель пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с целью пользователя"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GoalResponseDTO> updateUserGoal(@RequestBody @Valid UpdateGoalRequestDTO updateRequestDTO,
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        GoalResponseDTO goalResponseDTO = goalService.updateUserGoal(currentUser.getId(), updateRequestDTO);
        return ResponseEntity.ok(goalResponseDTO);
    }
    @Operation(
        summary = "Сбрасывает финансовую цель пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Цель успешно сброшена"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> resetUserGoal(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        goalService.resetUserGoal(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    @Operation(
        summary = "Удаляет все транзакции для цели")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Успешное удаление транзакций"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clearGoalTransactions(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        goalService.clearGoalTransactions(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Проверяет количество накопленных для достижения финансовый цели средств")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с количеством накопленных средств"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/saved")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CheckGoalResponseDTO> checkSavedToGoal(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        CheckGoalResponseDTO responseDTO = goalService.checkSavedToGoal(currentUser.getId());
        return ResponseEntity.ok(responseDTO);
    }
}
//TODO: refactor