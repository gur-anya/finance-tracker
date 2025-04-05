package org.ylabHomework.serviceClasses;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.AspectsRepository;

import jakarta.servlet.http.HttpSession;
/**
 * Аспект, логгирующий действия пользователей и замеряющий время логина. Все полученные данные вносятся в БД и выводятся
 * в консоль.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final AspectsRepository aspectsRepository;


    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void allGetMethods() {
    }

    @Around("allGetMethods()")
    public Object logGetRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String endpoint = attributes != null ? attributes.getRequest().getRequestURI() : "unknown endpoint";
        HttpSession session = attributes != null ? attributes.getRequest().getSession(false) : null;

        if (session != null) {
            User user = (User) session.getAttribute("loggedUser");
            if (user != null) {
                String action = "User entered endpoint: " + endpoint;
                System.out.println(user.getEmail() + ": " + action);
                aspectsRepository.putActionAudit(user.getEmail(), action);
            } else {
                String emailPlaceholder = "Guest";
                String action = "Guest entered endpoint: " + endpoint;
                System.out.println(action);
                aspectsRepository.putActionAudit(emailPlaceholder, action);
            }
        } else {
            String emailPlaceholder = "Guest";
            String action = "Guest entered endpoint: " + endpoint;
            System.out.println(action);
            aspectsRepository.putActionAudit(emailPlaceholder, action);
        }
        return joinPoint.proceed();
    }

    @Pointcut("execution(* org.ylabHomework.controllers.userControllers.AuthorizationController.loginUser(..)) && args(loginDTO, ..)")
    public void getLogin(LoginDTO loginDTO) {
    }

    @Around(value = "getLogin(loginDTO)", argNames = "joinPoint,loginDTO")
    @SuppressWarnings("unchecked")
    public Object logLogin(ProceedingJoinPoint joinPoint, LoginDTO loginDTO) throws Throwable {
        long startTime = System.currentTimeMillis();
        ResponseEntity<ResponseMessageDTO> response = (ResponseEntity<ResponseMessageDTO>) joinPoint.proceed();
        long loginTime = System.currentTimeMillis() - startTime;


        String email = loginDTO.getEmail();
        boolean success = response.getStatusCode() == HttpStatus.OK;
        if (success) {
            System.out.println(email + " logged in successfully, login took " + loginTime + " ms");
        } else {
            System.out.println(email + "tried to login, took " + loginTime + " ms");
        }

        aspectsRepository.putLoginAudit(email, loginTime, success);
        return response;
    }
}
