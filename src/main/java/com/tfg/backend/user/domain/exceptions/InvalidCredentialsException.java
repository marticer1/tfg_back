package com.tfg.backend.user.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
