package org.ylabHomework.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.serviceClasses.Constants;

@Api(value = "API главной страницы")
@Controller
@RequiredArgsConstructor
public class GreetingController {
    @ApiOperation(value = "Показать главную страницу",
            notes = "Перенаправляет на главную страницу приложения")
    @ApiResponse(code = 200, message = "Главная страница")
    @GetMapping(value = "/")
    public String showMainPage() {
        return Constants.INDEX_JSP;
    }
}
