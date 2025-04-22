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

@Tag(name = "API месячного бюджета")
@RestController()
@RequestMapping("/transactions/stats/monthlyBudget")
@RequiredArgsConstructor
public class MonthlyBudgetController {
    private final TransactionStatsService transactionStatsService;
    private final UserService userService;

    @Operation(
            summary = "Получить месячный бюджет пользователя",
            description = "Возвращает текущий месячный бюджет пользователя")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Полученный месячный бюджет"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/monthlyBudget")
    public ResponseEntity<?> getMonthlyBudget() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            double monthlyBudget = user.getMonthlyBudget();
            return ResponseEntity.ok().body(new FetchValueDTO(monthlyBudget));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Проверить состояние месячного бюджета",
            description = "Возвращает текущий бюджет и его остаток (или превышение)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Состояние бюджета"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/status")
    public ResponseEntity<?> checkMonthlyBudget() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            double budget = user.getMonthlyBudget();
            double status = 0;
            if (budget != 0.0) {
                status = transactionStatsService.calculateMonthlyBudgetLimit(user);
            }
            return ResponseEntity.ok()
                    .body(new FetchStatusDTO(budget, status));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }

    @Operation(
            summary = "Обновить месячный бюджет пользователя",
            description = "Обновляет месячный бюджет пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бюджет обновлен: Успешно обновлено!"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные. Сообщение содержит список ошибок валидации"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = "/update")
    public ResponseEntity<ResponseMessageDTO> updateMonthlyBudget(@RequestBody @Valid UpdateValueDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            user.setMonthlyBudget(dto.getNewValue());
            return ResponseEntity.ok()
                    .body(new ResponseMessageDTO("Месячный бюджет успешно обновлен!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }
}