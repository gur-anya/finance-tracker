package org.ylabHomework.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.serviceClasses.Constants;

@Tag(name = "API главной страницы")
@Controller
@RequiredArgsConstructor
public class GreetingController {
    @Operation(
            summary = "Показать главную страницу",
            description = "Перенаправляет на главную страницу приложения")
    @ApiResponse(responseCode = "200", description = "Главная страница")
    @GetMapping(value = "/")
    public String showMainPage() {
        return Constants.INDEX_JSP;
    }
}
