package com.tfg.backend.user.domain.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String field, String value) {
        super("User with " + field + " '" + value + "' already exists");
    }
}
