package com.tfg.backend.user.domain.exceptions;

public class CannotDeleteSelfException extends RuntimeException {
    
    public CannotDeleteSelfException() {
        super("Administrator cannot delete their own account");
    }
}
