package com.tfg.backend.user.application;

import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReadOneUserUseCase {
    
    private final UserRepositoryJPA userRepositoryJPA;
    private final UserMapper userMapper;
    
    public ReadOneUserUseCase(UserRepositoryJPA userRepositoryJPA, UserMapper userMapper) {
        this.userRepositoryJPA = userRepositoryJPA;
        this.userMapper = userMapper;
    }
    
    public ResponseUserDTO execute(UUID userId) {
        User user = userRepositoryJPA.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        return userMapper.fromObjectToDTO(user);
    }
    
    public ResponseUserDTO executeByUsername(String username) {
        User user = userRepositoryJPA.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        
        return userMapper.fromObjectToDTO(user);
    }
}
