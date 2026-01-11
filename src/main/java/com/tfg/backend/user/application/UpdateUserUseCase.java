package com.tfg.backend.user.application;

import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.dto.UpdateUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserAlreadyExistsException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class UpdateUserUseCase {
    
    private final UserRepositoryJPA userRepositoryJPA;
    private final UserMapper userMapper;
    
    public UpdateUserUseCase(UserRepositoryJPA userRepositoryJPA, UserMapper userMapper) {
        this.userRepositoryJPA = userRepositoryJPA;
        this.userMapper = userMapper;
    }
    
    public ResponseUserDTO execute(UUID userIdToUpdate, UpdateUserDTO updateUserDTO, UUID adminId) {
        User admin = userRepositoryJPA.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException();
        }
        
        User userToUpdate = userRepositoryJPA.findById(userIdToUpdate)
                .orElseThrow(() -> new UserNotFoundException(userIdToUpdate));
        
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(userToUpdate.getUsername())) {
            if (userRepositoryJPA.existsByUsername(updateUserDTO.getUsername())) {
                throw new UserAlreadyExistsException("username", updateUserDTO.getUsername());
            }
            userToUpdate.setUsername(updateUserDTO.getUsername());
        }
        
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(userToUpdate.getEmail())) {
            if (userRepositoryJPA.existsByEmail(updateUserDTO.getEmail())) {
                throw new UserAlreadyExistsException("email", updateUserDTO.getEmail());
            }
            userToUpdate.setEmail(updateUserDTO.getEmail());
        }
        
        if (updateUserDTO.getPassword() != null) {
            userToUpdate.setPassword(updateUserDTO.getPassword());
        }
        
        if (updateUserDTO.getRole() != null) {
            userToUpdate.setRole(updateUserDTO.getRole());
        }
        
        userToUpdate = userRepositoryJPA.save(userToUpdate);
        
        return userMapper.fromObjectToDTO(userToUpdate);
    }
}
