package org.ylabHomework.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
public class ServletLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServletLoggingAspect.class);


    @Pointcut("execution(* javax.servlet.http.HttpServlet+.do*(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)) && args(request, response)")
    public void servletExecution(HttpServletRequest request, HttpServletResponse response) {
    }

    @Around("servletExecution(request, response)")
    public Object logServletAccess(ProceedingJoinPoint joinPoint) throws Throwable {


        String servletName = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("Пользователь зашел в сервлет {}",
                servletName);
        System.out.println("ураа");
        return joinPoint.proceed();
    }
}
