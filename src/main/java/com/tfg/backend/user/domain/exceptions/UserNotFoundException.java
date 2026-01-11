package com.tfg.backend.user.domain.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(UUID userId) {
        super("User with id " + userId + " not found");
    }
    
    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found");
    }
}
