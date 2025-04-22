package org.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
public class LoggingAspect {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void allGetMethods() {
    }

    @Around("allGetMethods()")
    public Object logGetRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String endpoint = attributes != null ? attributes.getRequest().getRequestURI() : "unknown endpoint";

        String email = getEmail();

        String actor;
        if (email == null || email.isEmpty()) {
            actor = "Guest";
        } else {
            actor = email;
        }
        log.info("{} entered endpoint: {}", actor, endpoint);

        return joinPoint.proceed();
    }


    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}