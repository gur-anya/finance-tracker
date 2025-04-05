package org.ylabHomework.controllers.financeControllers.financeStatsControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.NewValueDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.StateAndParamDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

@Tag(name = "API месячного бюджета")
@Controller
@RequiredArgsConstructor
public class MonthlyBudgetController {
    private final TransactionStatsService transactionStatsService;

    @Operation(
            summary = "Получить месячный бюджет пользователя",
            description = "Возвращает текущий месячный бюджет пользователя")
    @ApiResponse(responseCode = "200", description = "Полученный месячный бюджет")
    @GetMapping(value = "/get_monthly_budget_management")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getMonthlyBudget(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        double budget = transactionStatsService.getMonthlyBudget(user);
        paramDTO.setParam(Double.toString(budget));
        return ResponseEntity.ok().body(paramDTO);
    }

    @Operation(
            summary = "Проверить состояние месячного бюджета",
            description = "Возвращает текущий бюджет и его остаток (или превышение)")
    @ApiResponse(responseCode = "200", description = "Состояние бюджета")
    @GetMapping(value = "/get_check_budget")
    @ResponseBody
    public ResponseEntity<StateAndParamDTO> checkBudget(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        StateAndParamDTO dto = new StateAndParamDTO();
        double budgetState = transactionStatsService.checkMonthlyBudgetLimit(user);
        double monthlyBudget = transactionStatsService.getMonthlyBudget(user);
        dto.setState(Double.toString(budgetState));
        dto.setParam(Double.toString(monthlyBudget));
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Обновить месячный бюджет пользователя",
            description = "Обновляет месячный бюджет пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бюджет обновлен: Успешно обновлено!"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные. Сообщение содержит список ошибок валидации")
    })
    @PostMapping(value = "/update_budget")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> updateBudget(
            @RequestBody @Valid NewValueDTO newValueDTO,
            BindingResult result,
            HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        StringBuilder stateMessageBuilder = new StringBuilder();
        String stateMessage;
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.badRequest().body(responseMessageDTO);
        } else {
            double newBudget = newValueDTO.getNewValue();
            transactionStatsService.setMonthlyBudget(user, newBudget);
            responseMessageDTO.setMessage("Успешно обновлено!");
            return ResponseEntity.ok().body(responseMessageDTO);
        }
    }
}

