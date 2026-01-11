package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.domain.exceptions.CannotDeleteSelfException;
import com.tfg.backend.user.domain.exceptions.InvalidCredentialsException;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserAlreadyExistsException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class UserExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedOperationException(UnauthorizedOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(CannotDeleteSelfException.class)
    public ResponseEntity<Map<String, String>> handleCannotDeleteSelfException(CannotDeleteSelfException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }
}
