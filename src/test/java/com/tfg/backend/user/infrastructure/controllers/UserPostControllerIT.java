package com.tfg.backend.user.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.RegistrationUserDTO;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class UserPostControllerIT {

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
    void createUser_returnsCreatedResponse() throws Exception {
        RegistrationUserDTO requestDto = UserMother.validRegistrationUserDTO();

        mockMvc.perform(post("/users")
                        .header("X-Admin-Id", adminId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(requestDto.getUsername()))
                .andExpect(jsonPath("$.email").value(requestDto.getEmail()))
                .andExpect(jsonPath("$.role").value(requestDto.getRole().toString()));
    }

    @Test
    void createInitialAdmin_returnsCreatedResponse() throws Exception {
        userRepositoryJPA.deleteAll();
        
        RegistrationUserDTO requestDto = UserMother.validRegistrationAdminDTO();

        mockMvc.perform(post("/users/initial-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(requestDto.getUsername()))
                .andExpect(jsonPath("$.email").value(requestDto.getEmail()))
                .andExpect(jsonPath("$.role").value(Role.ADMIN.toString()));
    }
}
