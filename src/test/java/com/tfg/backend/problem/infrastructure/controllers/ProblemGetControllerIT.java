package com.tfg.backend.problem.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfig.class)
public class ProblemGetControllerIT {

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
    private UUID problemId;
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
        problemId = problem.getId();
    }

    @Test
    void getAllProblemsFromCollection_returnsOkResponse() throws Exception {
        mockMvc.perform(get("/problem-collections/{collectionId}/problems", collectionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(problemId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Problem"));
    }

    @Test
    void getProblemById_returnsOkResponse() throws Exception {
        mockMvc.perform(get("/problems/{problemId}", problemId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(problemId.toString()))
                .andExpect(jsonPath("$.name").value("Test Problem"))
                .andExpect(jsonPath("$.valueBestKnownSolution").value(100))
                .andExpect(jsonPath("$.numberRuns").value(10));
    }
}
