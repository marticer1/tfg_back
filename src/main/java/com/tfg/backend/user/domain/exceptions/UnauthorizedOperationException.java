package com.tfg.backend.user.domain.exceptions;

public class UnauthorizedOperationException extends RuntimeException {
    
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException() {
        super("Only administrators can perform this operation");
    }
}
