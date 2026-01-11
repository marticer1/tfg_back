package com.tfg.backend.user.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.user.application.dto.LoginRequestDTO;
import com.tfg.backend.user.domain.Role;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class AuthControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepositoryJPA.save(user);
    }

    @Test
    void login_withValidCredentials_returnsOkResponse() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
