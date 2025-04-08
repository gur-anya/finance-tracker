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
@Api(value = "API финансовой цели")
@Controller
@RequiredArgsConstructor
public class GoalController {
    private final TransactionStatsService transactionStatsService;

    @ApiOperation(value = "Получить цель пользователя",
            notes = "Возвращает текущую финансовую цель пользователя")
    @ApiResponse(code = 200, message = "Полученная цель", response = SingleParamDTO.class)
    @GetMapping(value = "/get_goal_management")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getGoal(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        double goal = transactionStatsService.getGoal(user);
        paramDTO.setParam(Double.toString(goal));
        return ResponseEntity.ok().body(paramDTO);
    }


    @ApiOperation(value = "Проверить прогресс достижения цели",
            notes = "Возвращает текущую цель и прогресс её достижения")
    @ApiResponse(code = 200, message = "Прогресс по цели", response = StateAndParamDTO.class)
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


    @ApiOperation(value = "Обновить цель пользователя",
            notes = "Обновляет финансовую цель пользователя. В случае ошибки валидации возвращает список ошибок.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Цель обновлена: Успешно обновлено!", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Некорректные входные данные. Сообщение содержит список ошибок валидации", response = ResponseMessageDTO.class)
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