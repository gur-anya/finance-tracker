package org.ylabHomework.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ylabHomework.repositories.TokenRepository;
import org.ylabHomework.serviceClasses.customExceptions.TokenException;
import org.ylabHomework.serviceClasses.springConfigs.security.JWTCore;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository repository;
    private final JWTCore jwtCore;


    /**
     * Добавляет токен в чёрный список.
     *
     * @param token токен для добавления
     * @throws IllegalArgumentException если токен недействителен
     */
    public void blacklistToken(String token) {
        try {
            jwtCore.getEmailFromJwt(token);
            repository.addToken(token);

        } catch (Exception e) {
            throw new TokenException(e);
        }
    }

    /**
     * Проверяет, находится ли токен в чёрном списке.
     *
     * @param token токен для проверки
     * @return true, если токен в чёрном списке, иначе false
     */
    public boolean isTokenBlacklisted(String token) {
        return repository.getToken(token) != null;
    }
}