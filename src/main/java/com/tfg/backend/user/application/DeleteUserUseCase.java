package com.tfg.backend.user.application;

import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.CannotDeleteSelfException;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class DeleteUserUseCase {
    
    private final UserRepositoryJPA userRepositoryJPA;
    
    public DeleteUserUseCase(UserRepositoryJPA userRepositoryJPA) {
        this.userRepositoryJPA = userRepositoryJPA;
    }
    
    public void execute(UUID userIdToDelete, UUID adminId) {
        User admin = userRepositoryJPA.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException();
        }
        
        if (userIdToDelete.equals(adminId)) {
            throw new CannotDeleteSelfException();
        }
        
        if (!userRepositoryJPA.existsById(userIdToDelete)) {
            throw new UserNotFoundException(userIdToDelete);
        }
        
        userRepositoryJPA.deleteById(userIdToDelete);
    }
}
