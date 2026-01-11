package com.tfg.backend.user;

import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.UpdateUserDTO;
import com.tfg.backend.user.domain.Role;
import com.tfg.backend.user.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMother {
    
    public static User validAdminUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .email("admin@example.com")
                .password("admin123")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static User validRegularUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .email("user@example.com")
                .password("user123")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public static RegistrationUserDTO validRegistrationUserDTO() {
        return RegistrationUserDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
    }
    
    public static RegistrationUserDTO validRegistrationAdminDTO() {
        return RegistrationUserDTO.builder()
                .username("newadmin")
                .email("newadmin@example.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();
    }
    
    public static UpdateUserDTO validUpdateUserDTO() {
        return UpdateUserDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .password("newpassword123")
                .role(Role.USER)
                .build();
    }
}
