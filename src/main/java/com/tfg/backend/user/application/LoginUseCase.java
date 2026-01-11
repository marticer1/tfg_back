package com.tfg.backend.user.application;

import com.tfg.backend.security.JwtUtil;
import com.tfg.backend.user.application.dto.LoginRequestDTO;
import com.tfg.backend.user.application.dto.LoginResponseDTO;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.InvalidCredentialsException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginUseCase {
    
    private final UserRepositoryJPA userRepositoryJPA;
    private final JwtUtil jwtUtil;
    
    public LoginUseCase(UserRepositoryJPA userRepositoryJPA, JwtUtil jwtUtil) {
        this.userRepositoryJPA = userRepositoryJPA;
        this.jwtUtil = jwtUtil;
    }
    
    public LoginResponseDTO execute(LoginRequestDTO loginRequest) {
        Optional<User> userOptional = userRepositoryJPA.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        
        User user = userOptional.get();
        
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .token(token)
                .build();
    }
}
