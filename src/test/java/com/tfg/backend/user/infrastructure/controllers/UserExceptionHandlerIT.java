package com.tfg.backend.user.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.LoginRequestDTO;
import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.UpdateUserDTO;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class UserExceptionHandlerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    private UUID adminId;

    @BeforeEach
    void setUp() {
        User admin = UserMother.validAdminUser();
        admin = userRepositoryJPA.save(admin);
        adminId = admin.getId();
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/users/{userId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_alreadyExists_returns409() throws Exception {
        User existingUser = UserMother.validRegularUser();
        userRepositoryJPA.save(existingUser);
        
        RegistrationUserDTO dto = RegistrationUserDTO.builder()
                .username(existingUser.getUsername())
                .email("different@example.com")
                .password("password123")
                .role(existingUser.getRole())
                .build();

        mockMvc.perform(post("/users")
                        .header("X-Admin-Id", adminId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("nonexistent@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_nonAdmin_returns403() throws Exception {
        User regularUser = UserMother.validRegularUser();
        regularUser = userRepositoryJPA.save(regularUser);
        
        UpdateUserDTO updateDto = UserMother.validUpdateUserDTO();

        mockMvc.perform(put("/users/{userId}", regularUser.getId())
                        .header("X-Admin-Id", regularUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_self_returns400() throws Exception {
        mockMvc.perform(delete("/users/{userId}", adminId)
                        .header("X-Admin-Id", adminId.toString()))
                .andExpect(status().isBadRequest());
    }
}
