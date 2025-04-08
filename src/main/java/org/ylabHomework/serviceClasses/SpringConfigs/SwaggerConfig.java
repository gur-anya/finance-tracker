package org.ylabHomework.serviceClasses.SpringConfigs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * Класс для настройки Swagger. Настраивает основную информацию об API, определяет пакет контроллеров, хост и порт
 * для доступа к API, ресурсы для Swagger UI.
 *
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
@ComponentScan(basePackages = "org.ylabHomework")
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.ylabHomework.controllers"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(new springfox.documentation.service.ApiInfo(
                        "Документация API для приложения \"Личный трекер финансов\"",
                        "Описание всех эндпоинтов, использующихся в приложении, с объяснением возвращемых статусов и кодов.",
                        "1.0.0",
                        null,
                        new Contact("Гурьева Анна", "https://github.com/anya-ananasss", "an.an.gurieva@yandex.ru"),
                        null,
                        null,
                        new java.util.ArrayList<>()
                ))
                .protocols(new java.util.HashSet<>(java.util.Arrays.asList("http", "https")))
                .host("localhost:8080");
    }
}