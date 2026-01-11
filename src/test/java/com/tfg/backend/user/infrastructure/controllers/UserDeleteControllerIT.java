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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class UserDeleteControllerIT {

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
    void deleteUser_returnsNoContentResponse() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId)
                        .header("X-Admin-Id", adminId.toString()))
                .andExpect(status().isNoContent());

        assertThat(userRepositoryJPA.existsById(userId)).isFalse();
    }
}
