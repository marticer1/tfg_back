package com.tfg.backend.algorithm.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.UpdateAlgorithmDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import com.tfg.backend.problem.domain.ContinuousProblem;
import com.tfg.backend.problem.domain.Problem;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class AlgorithmPutControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @Autowired
    private ProblemRepositoryJPA problemRepositoryJPA;

    @Autowired
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @Autowired
    private TestSecurityHelper testSecurityHelper;

    private UUID algorithmId;
    private String token;

    @BeforeEach
    void setUp() {
        token = testSecurityHelper.createTestUserAndGetToken();
        
        ProblemCollection collection = new ProblemCollection();
        collection.setId(UUID.randomUUID());
        collection.setName("Test Collection");
        collection.setColor(Color.BLUE);
        collection = problemCollectionRepositoryJPA.save(collection);

        Problem problem = new ContinuousProblem();
        problem.setId(UUID.randomUUID());
        problem.setName("Test Problem");
        problem.setColor(Color.RED);
        problem.setValueBestKnownSolution(100);
        problem.setNumberRuns(10);
        problem.setVertexSize(1.0);
        problem.setArrowSize(0.5);
        problem.setTreeLayout(false);
        problem.setMaximization(true);
        problem.setProblemCollection(collection);
        problem = problemRepositoryJPA.save(problem);

        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Test Algorithm");
        algorithm.setColor(Color.GREEN);
        algorithm.setProblem(problem);
        algorithm = algorithmRepositoryJPA.save(algorithm);
        algorithmId = algorithm.getId();
    }

    @Test
    void updateAlgorithm_returnsOkResponse() throws Exception {
        UpdateAlgorithmDTO updateDto = AlgorithmMother.validUpdateAlgorithmDTO();

        mockMvc.perform(put("/algorithms/{id}", algorithmId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(algorithmId.toString()))
                .andExpect(jsonPath("$.name").value(updateDto.getName()));
    }
}
