package com.tfg.backend.user.application.mapper;

import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.domain.Role;
import com.tfg.backend.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserMapper {
    
    public User fromDTOtoObject(RegistrationUserDTO dto) {
        return User.builder()
                .id(UUID.randomUUID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole() != null ? dto.getRole() : Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public ResponseUserDTO fromObjectToDTO(User user) {
        return ResponseUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
