package org.ylabHomework.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;

@Aspect
public class ServletLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServletLoggingAspect.class);



    @Pointcut("execution(* org.ylabHomework.controllers.servlets..*.doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)) && args(request, response)")
    public void servletExecution(HttpServletRequest request, HttpServletResponse response) {

    }

    @Around("servletExecution(request, response)")
    public Object logServletAccess(ProceedingJoinPoint joinPoint,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Throwable {
        String servletName = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("User entered the servlet {}", servletName);
        return joinPoint.proceed();
    }
    @Pointcut("execution(* org.ylabHomework.controllers.servlets.userServlets.LoginServlet.doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)) && args(request, response)")
    public void loginMeasurement(HttpServletRequest request, HttpServletResponse response) {

    }

    @Around("loginMeasurement(request, response)")
    public Object logLoginTime(ProceedingJoinPoint joinPoint,
                               HttpServletRequest request,
                               HttpServletResponse response) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long loginTime = System.currentTimeMillis() - startTime;
        logger.info("Login took {} ms", loginTime);
        return result;
    }
}