package com.tfg.backend.user.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class UserGetControllerIT {

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
    void getAllUsers_returnsOkResponse() throws Exception {
        mockMvc.perform(get("/users")
                        .header("X-Admin-Id", adminId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserById_returnsOkResponse() throws Exception {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }
}
