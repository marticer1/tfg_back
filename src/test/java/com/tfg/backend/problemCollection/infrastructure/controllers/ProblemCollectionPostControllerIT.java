package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.problemCollection.ProblemCollectionMother;
import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import com.tfg.backend.security.TestSecurityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class ProblemCollectionPostControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @Autowired
    private TestSecurityHelper testSecurityHelper;

    private String token;

    @BeforeEach
    void setUp() {
        token = testSecurityHelper.createTestUserAndGetToken();
    }

    @Test
    void createProblemCollection_returnsCreatedResponse() throws Exception {
        RegistrationProblemCollectionDTO requestDto = ProblemCollectionMother.validRegistrationProblemCollectionDTO();

        mockMvc.perform(post("/problem-collections")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }
}
