package com.tfg.backend.problem.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.problem.ProblemMother;
import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
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

import java.awt.Color;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class ProblemPostControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @Autowired
    private ProblemRepositoryJPA problemRepositoryJPA;

    @Autowired
    private TestSecurityHelper testSecurityHelper;

    private UUID collectionId;
    private String token;

    @BeforeEach
    void setUp() {
        token = testSecurityHelper.createTestUserAndGetToken();
        
        ProblemCollection collection = new ProblemCollection();
        collection.setId(UUID.randomUUID());
        collection.setName("Test Collection");
        collection.setColor(Color.BLUE);
        collection = problemCollectionRepositoryJPA.save(collection);
        collectionId = collection.getId();
    }

    @Test
    void createDiscreteProblem_returnsCreatedResponse() throws Exception {
        // Arrange
        RegistrationProblemDTO requestDto = ProblemMother.validRegistrationDiscreteProblemDTO();

        // Act and Assert
        mockMvc.perform(post("/problem-collections/{collectionId}/problems", collectionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(requestDto.getName()))
                .andExpect(jsonPath("$.valueBestKnownSolution").value(requestDto.getValueBestKnownSolution()))
                .andExpect(jsonPath("$.numberRuns").value(requestDto.getNumberRuns()))
                .andExpect(jsonPath("$.vertexSize").value(requestDto.getVertexSize()))
                .andExpect(jsonPath("$.arrowSize").value(requestDto.getArrowSize()))
                .andExpect(jsonPath("$.treeLayout").value(requestDto.getTreeLayout()));
    }

    @Test
    void createContinuousProblem_returnsCreatedResponse() throws Exception {
        // Arrange
        RegistrationProblemDTO requestDto = ProblemMother.validRegistrationContinuousProblemDTO();

        // Act and Assert
        mockMvc.perform(post("/problem-collections/{collectionId}/problems", collectionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(requestDto.getName()))
                .andExpect(jsonPath("$.valueBestKnownSolution").value(requestDto.getValueBestKnownSolution()))
                .andExpect(jsonPath("$.numberRuns").value(requestDto.getNumberRuns()))
                .andExpect(jsonPath("$.vertexSize").value(requestDto.getVertexSize()))
                .andExpect(jsonPath("$.arrowSize").value(requestDto.getArrowSize()))
                .andExpect(jsonPath("$.treeLayout").value(requestDto.getTreeLayout()));
    }

    @Test
    void createProblem_withAlgorithms_returnsCreatedResponse() throws Exception {
        // Arrange
        RegistrationProblemDTO requestDto = ProblemMother.validRegistrationDiscreteProblemDTO();
        requestDto.setAlgorithms(java.util.List.of(
                com.tfg.backend.algorithm.AlgorithmMother.validRegistrationAlgorithmDTOWithoutFile()
        ));

        // Act and Assert
        mockMvc.perform(post("/problem-collections/{collectionId}/problems", collectionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }
}
