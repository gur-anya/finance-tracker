package org.ylabHomework.controllers.filters;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 *  Фильтр, запрещающий неавторизованным пользователям доступ на страницы для авторизованных пользователей. При попытке
 *  гостя попасть на такую страницу он перенаправляется на страницу входа.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 30.03.2025
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);

        boolean nonAuthorisedPage = path.equals("/") || path.equals("/login") || path.equals("/registration") ||
                path.contains("/swagger-ui/index.html") || path.contains("/v2/api-docs");

        if (nonAuthorisedPage || (session != null && session.getAttribute("loggedUser") != null)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect("/login");
        }
    }
}