package org.ylabHomework.serviceClasses.springConfigs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.ylabHomework.DTOs.ErrorResponse;
import org.ylabHomework.services.TokenService;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final JWTCore jwtCore;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("TokenFilter invoked for request: {}", request.getRequestURI());
        String jwt = null;
        String email = null;
        UserDetails userDetails = null;
        UsernamePasswordAuthenticationToken auth = null;
        String headerAuth = request.getHeader("Authorization");
        log.info("Authorization header received: {}", headerAuth);
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
            log.info("Extracted JWT: {}", jwt);
        } else {
            log.warn("Invalid or missing Authorization header");
        }

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
            log.debug("Extracted JWT: {}", jwt);
        }

        if (jwt != null) {
            if (tokenService.isTokenBlacklisted(jwt)) {
                log.warn("Token is blacklisted: {}", jwt);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Токен недействителен! Пожалуйста, войдите в аккаунт еще раз!");
                return;
            }
            try {
                email = jwtCore.getEmailFromJwt(jwt);
                log.debug("Extracted email from JWT: {}", email);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    log.debug("Loaded UserDetails: {}", userDetails.getUsername());
                    auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    log.info("Created authentication: {}", auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Authentication set in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
                }
            } catch (ExpiredJwtException e) {
                log.warn("Expired JWT: {}", e.getMessage());
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Токен истёк! Пожалуйста, войдите в аккаунт еще раз!");
                return;
            } catch (MalformedJwtException e) {
                log.warn("Malformed JWT: {}", e.getMessage());
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Неверный или повреждённый токен!");
                return;
            } catch (Exception e) {
                log.error("Authentication error: {}", e.getMessage(), e);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Ошибка аутентификации!");
                return;
            }
        } else {
            log.debug("No valid JWT found in request");
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status.value());
        new ObjectMapper().writeValue(response.getOutputStream(), new ErrorResponse(message, LocalDateTime.now()));
    }
}
