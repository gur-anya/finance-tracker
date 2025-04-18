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

@Tag(name = "API финансовой цели")
@Controller
@RequiredArgsConstructor
public class GoalController {
    private final TransactionStatsService transactionStatsService;

    @Operation(
            summary = "Получить цель пользователя",
            description = "Возвращает текущую финансовую цель пользователя")
    @ApiResponse(responseCode = "200", description = "Полученная цель")
    @GetMapping(value = "/get_goal_management")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getGoal(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        double goal = transactionStatsService.getGoal(user);
        paramDTO.setParam(Double.toString(goal));
        return ResponseEntity.ok().body(paramDTO);
    }


    @Operation(
            summary = "Проверить прогресс достижения цели",
            description = "Возвращает текущую цель и прогресс её достижения")
    @ApiResponse(responseCode = "200", description = "Прогресс по цели")
    @GetMapping(value = "/get_check_goal")
    @ResponseBody
    public ResponseEntity<StateAndParamDTO> checkGoal(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        StateAndParamDTO dto = new StateAndParamDTO();
        double goalState = transactionStatsService.checkGoalProgress(user);
        double goal = transactionStatsService.getGoal(user);
        dto.setState(Double.toString(goalState));
        dto.setParam(Double.toString(goal));
        return ResponseEntity.ok().body(dto);
    }


    @Operation(
            summary = "Обновить цель пользователя",
            description = "Обновляет финансовую цель пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цель обновлена: Успешно обновлено!"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные. Сообщение содержит список ошибок валидации")
    })
    @PostMapping(value = "/update_goal")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> updateGoal(
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
            double newGoal = newValueDTO.getNewValue();
            transactionStatsService.setGoal(user, newGoal);
            responseMessageDTO.setMessage("Успешно обновлено!");
            return ResponseEntity.ok().body(responseMessageDTO);
        }
    }
}