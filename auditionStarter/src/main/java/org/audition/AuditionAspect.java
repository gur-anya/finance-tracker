package org.audition;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@ConfigurationProperties(prefix = "audition-starter")
public class AuditionAspect {
    @Pointcut("execution(* *..*Controller.login*(..))")
    public void loginMethods() {
    }

    @Around(value = "loginMethods()")
    public Object auditLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        long loginTime = System.currentTimeMillis() - startTime;

        String email = getEmail();
        if (email == null || email.isEmpty()) {
            log.error("There was an error during login audition!");
        } else {

            boolean success = isLoginSuccessful(result);

            if (success) {
                log.info("{} logged in successfully, login took {} ms", email, loginTime);
            } else {
                log.warn("{} tried to login, login took {} ms", email, loginTime);
            }

        }
        return result;
    }

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    private boolean isLoginSuccessful(Object result) {
        if (result instanceof ResponseEntity<?> response) {
            return response.getStatusCode() == HttpStatus.OK;
        }
        return false;
    }
}
