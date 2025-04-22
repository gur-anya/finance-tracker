package org.ylabHomework.controllers.financeControllers.financeStatsControllers;

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
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.FetchStatusDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.FetchValueDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.UpdateValueDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;
import org.ylabHomework.services.UserService;

@Tag(name = "API финансовой цели")
@RestController()
@RequestMapping("/transactions/stats/goal")
@RequiredArgsConstructor
public class GoalController {
    private final TransactionStatsService transactionStatsService;
    private final UserService userService;

    @Operation(
            summary = "Получить цель пользователя",
            description = "Возвращает текущую финансовую цель пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Полученная цель"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/goal")
    public ResponseEntity<?> getGoal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            double goal = user.getGoal();
            return ResponseEntity.ok().body(new FetchValueDTO(goal));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Проверить прогресс достижения цели",
            description = "Возвращает текущую цель и прогресс её достижения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прогресс по цели"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/status")
    public ResponseEntity<?> checkGoal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();
            double goal = user.getGoal();
            double status = 0;
            if (goal != 0.0) {
                status = transactionStatsService.calculateGoalProgress(userId);
            }
            return ResponseEntity.ok()
                    .body(new FetchStatusDTO(goal, status));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Обновить цель пользователя",
            description = "Обновляет финансовую цель пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цель обновлена: Успешно обновлено!"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные. Сообщение содержит список ошибок валидации"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = "/update")
    public ResponseEntity<ResponseMessageDTO> updateGoal(@RequestBody @Valid UpdateValueDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            user.setGoal(dto.getNewValue());
            return ResponseEntity.ok()
                    .body(new ResponseMessageDTO("Цель успешно обновлена!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }
}