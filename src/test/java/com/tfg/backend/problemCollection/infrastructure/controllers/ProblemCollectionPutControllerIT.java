package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.problemCollection.ProblemCollectionMother;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
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
public class ProblemCollectionPutControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

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
    void updateProblemCollection_returnsOkResponse() throws Exception {
        UpdateProblemCollectionDTO updateDto = ProblemCollectionMother.validUpdateProblemCollectionDTO();

        mockMvc.perform(put("/problem-collections/{id}", collectionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(collectionId.toString()))
                .andExpect(jsonPath("$.name").value(updateDto.getName()));
    }
}
