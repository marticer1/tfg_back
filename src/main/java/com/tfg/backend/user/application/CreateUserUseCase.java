package com.tfg.backend.user.application;

import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.Role;
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
public class CreateUserUseCase {
    
    private final UserMapper userMapper;
    private final UserRepositoryJPA userRepositoryJPA;
    
    public CreateUserUseCase(UserMapper userMapper, UserRepositoryJPA userRepositoryJPA) {
        this.userMapper = userMapper;
        this.userRepositoryJPA = userRepositoryJPA;
    }
    
    public ResponseUserDTO execute(RegistrationUserDTO registrationUserDTO, UUID adminId) {
        User admin = userRepositoryJPA.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        
        if (!admin.isAdmin()) {
            throw new UnauthorizedOperationException();
        }
        
        if (userRepositoryJPA.existsByUsername(registrationUserDTO.getUsername())) {
            throw new UserAlreadyExistsException("username", registrationUserDTO.getUsername());
        }
        
        if (userRepositoryJPA.existsByEmail(registrationUserDTO.getEmail())) {
            throw new UserAlreadyExistsException("email", registrationUserDTO.getEmail());
        }
        
        User user = userMapper.fromDTOtoObject(registrationUserDTO);
        user = userRepositoryJPA.save(user);
        
        return userMapper.fromObjectToDTO(user);
    }
    
    public ResponseUserDTO executeInitialAdmin(RegistrationUserDTO registrationUserDTO) {
        if (userRepositoryJPA.existsByUsername(registrationUserDTO.getUsername())) {
            throw new UserAlreadyExistsException("username", registrationUserDTO.getUsername());
        }
        
        if (userRepositoryJPA.existsByEmail(registrationUserDTO.getEmail())) {
            throw new UserAlreadyExistsException("email", registrationUserDTO.getEmail());
        }
        
        registrationUserDTO.setRole(Role.ADMIN);
        User user = userMapper.fromDTOtoObject(registrationUserDTO);
        user = userRepositoryJPA.save(user);
        
        return userMapper.fromObjectToDTO(user);
    }
}
