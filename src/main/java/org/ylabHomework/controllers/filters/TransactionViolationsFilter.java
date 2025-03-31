package org.ylabHomework.controllers.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.ActionsWithTransactionDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.models.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter({"/create_transaction", "/update_transaction"})
public class TransactionViolationsFilter implements Filter {
    private Validator validator;
    private ObjectMapper objectMapper;
    private String userEmail;

    @Override
    public void init(FilterConfig filterConfig) {
        this.validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
        this.objectMapper = new ObjectMapper();

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        User user = (User) httpRequest.getServletContext().getAttribute("user");
        if (user == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(parseJsonResponse("Пользователь не авторизован"));
            return;
        }

        this.userEmail = user.getEmail();

        StringBuilder sb = new StringBuilder();
        String line;


        try (BufferedReader reader = httpRequest.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String data = sb.toString();


        if (!data.isEmpty() && httpRequest.getRequestURI().contains("transaction")) {
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
        if (httpRequest.getRequestURL().toString().contains("/create_transaction")) {
            BasicTransactionDTO transactionDTO = this.objectMapper.readValue(data, BasicTransactionDTO.class);
            transactionDTO.setUserEmail(userEmail);
            violations = this.validator.validate(transactionDTO);
            httpRequest.setAttribute("DTO", transactionDTO);
        } else if (httpRequest.getRequestURL().toString().contains("/update_transaction")) {
            ActionsWithTransactionDTO transactionDTO = this.objectMapper.readValue(data, ActionsWithTransactionDTO.class);
            transactionDTO.setUserEmail(userEmail);
            violations = this.validator.validate(transactionDTO);
            httpRequest.setAttribute("DTO", transactionDTO);
        }
        return violations;
    }
}