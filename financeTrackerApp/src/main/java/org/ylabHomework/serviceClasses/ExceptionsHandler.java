package org.ylabHomework.serviceClasses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.serviceClasses.customExceptions.CustomDatabaseException;
import org.ylabHomework.serviceClasses.customExceptions.EmailAlreadyExistsException;
import org.ylabHomework.serviceClasses.customExceptions.TokenException;

@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessageDTO> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder stateMessageBuilder = new StringBuilder();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
        }
        String stateMessage = stateMessageBuilder.toString().trim();
        return ResponseEntity.badRequest().body(new ResponseMessageDTO(stateMessage));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseMessageDTO> handleEmailAlreadyExistsException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessageDTO("Пользователь с таким email уже зарегистрирован!"));
    }

    @ExceptionHandler(CustomDatabaseException.class)
    public ResponseEntity<ResponseMessageDTO> handleSQLException(CustomDatabaseException e) {
        log.error("Ошибка при обращении к базе данных: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ResponseMessageDTO("Произошла ошибка при обращении к базе данных. Подробная информация - в логах"));
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ResponseMessageDTO> handleTokenException(TokenException e) {
        log.error("Ошибка при работе с токеном: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ResponseMessageDTO("Произошла ошибка при работе с токеном JWT. Подробная информация - в логах"));
    }


    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ResponseMessageDTO> handleServiceException(Exception e) {
        return ResponseEntity.badRequest().body(new ResponseMessageDTO(e.getMessage()));
    }
}