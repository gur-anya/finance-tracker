package org.ylabHomework.controllers.financeControllers.financeStatsControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Api(value = "API месячного бюджета")
@Controller
@RequiredArgsConstructor
public class MonthlyBudgetController {
    private final TransactionStatsService transactionStatsService;

    @ApiOperation(value = "Получить месячный бюджет пользователя",
            notes = "Возвращает текущий месячный бюджет пользователя")
    @ApiResponse(code = 200, message = "Полученный месячный бюджет", response = SingleParamDTO.class)
    @GetMapping(value = "/get_monthly_budget_management")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getMonthlyBudget(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        double budget = transactionStatsService.getMonthlyBudget(user);
        paramDTO.setParam(Double.toString(budget));
        return ResponseEntity.ok().body(paramDTO);
    }

    @ApiOperation(value = "Проверить состояние месячного бюджета",
            notes = "Возвращает текущий бюджет и его остаток (или превышение)")
    @ApiResponse(code = 200, message = "Состояние бюджета", response = StateAndParamDTO.class)
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

    @ApiOperation(value = "Обновить месячный бюджет пользователя",
            notes = "Обновляет месячный бюджет пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Бюджет обновлен: Успешно обновлено!", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Некорректные входные данные. Сообщение содержит список ошибок валидации",
                    response = ResponseMessageDTO.class)
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

