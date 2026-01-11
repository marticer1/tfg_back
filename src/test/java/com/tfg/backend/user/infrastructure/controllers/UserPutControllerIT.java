package com.tfg.backend.user.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.user.UserMother;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class UserPutControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    private UUID userId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        User admin = UserMother.validAdminUser();
        admin = userRepositoryJPA.save(admin);
        adminId = admin.getId();

        User user = UserMother.validRegularUser();
        user = userRepositoryJPA.save(user);
        userId = user.getId();
    }

    @Test
    void updateUser_returnsOkResponse() throws Exception {
        UpdateUserDTO updateDto = UserMother.validUpdateUserDTO();

        mockMvc.perform(put("/users/{userId}", userId)
                        .header("X-Admin-Id", adminId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(updateDto.getUsername()))
                .andExpect(jsonPath("$.email").value(updateDto.getEmail()));
    }
}
