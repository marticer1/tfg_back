package com.tfg.backend.user.application;

import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReadAllUsersUseCase {
    
    private final UserRepositoryJPA userRepositoryJPA;
    private final UserMapper userMapper;
    
    public ReadAllUsersUseCase(UserRepositoryJPA userRepositoryJPA, UserMapper userMapper) {
        this.userRepositoryJPA = userRepositoryJPA;
        this.userMapper = userMapper;
    }
    
    public List<ResponseUserDTO> execute(UUID adminId) {
        User admin = userRepositoryJPA.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException();
        }
        
        return userRepositoryJPA.findAll()
                .stream()
                .map(userMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
