package org.ylabHomework.controllers.filters;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter({"/login", "/registration", "/update_account"})
public class UserViolationsFilter implements Filter {
    private Validator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) {
            this.validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
            this.objectMapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;


        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = httpRequest.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String data = sb.toString();


        if (!data.isEmpty() && !httpRequest.getRequestURI().contains("habit")) {
            Set<ConstraintViolation<Object>> violations = initDTOUsage(httpRequest, data);
            if (!violations.isEmpty()) {
                StringBuilder errorsSb = new StringBuilder();
                for (ConstraintViolation<Object> violation : violations) {
                    errorsSb.append(violation.getMessage()).append(" ");
                }

                httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
                httpResponse.setContentType("application/json");
                String errors = errorsSb.toString();
                httpResponse.getWriter().write(parseJsonResponse(errors));
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {

    }

    private String parseJsonResponse(String toJson) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO(toJson);
        return new ObjectMapper().writeValueAsString(responseMessageDTO);
    }

    private Set<ConstraintViolation<Object>> initDTOUsage(HttpServletRequest httpRequest, String data) throws JsonProcessingException {

        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        if (httpRequest.getRequestURL().toString().contains("/registration")) {
            BasicUserDTO userDTO = this.objectMapper.readValue(data, BasicUserDTO.class);
            violations = this.validator.validate(userDTO);
            httpRequest.setAttribute("DTO", userDTO);
        } else if (httpRequest.getRequestURL().toString().contains("/login")) {
            LoginDTO loginDTO = this.objectMapper.readValue(data, LoginDTO.class);
            violations = this.validator.validate(loginDTO);
            httpRequest.setAttribute("DTO", loginDTO);
        } else if (httpRequest.getRequestURL().toString().contains("/update_account")) {
            try {
                ActionsWithUserDTO emailDTO = this.objectMapper.readValue(data, ActionsWithUserDTO.class);
                violations = this.validator.validate(emailDTO);
                httpRequest.setAttribute("DTO", emailDTO);
                httpRequest.setAttribute("json", data);
            } catch (RuntimeException e) {
                System.out.println("Ошибка! " + e.getMessage());
            }
        }
        return violations;
    }
}
