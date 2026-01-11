package com.tfg.backend.security;

import com.tfg.backend.user.domain.Role;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TestSecurityHelper {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    public String createTestUserAndGetToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepositoryJPA.save(user);
        
        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }
}
