package com.tfg.backend.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.backend.algorithm.application.CreateEdgeUseCase;
import com.tfg.backend.algorithm.application.CreateNodeUseCase;
import com.tfg.backend.algorithm.application.dto.Position3DDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.NodeType;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import com.tfg.backend.algorithm.infrastructure.repositories.EdgeRepositoryJPA;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizationServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CreateNodeUseCase createNodeUseCase;

    @Mock
    private CreateEdgeUseCase createEdgeUseCase;

    @Mock
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @Mock
    private NodeRepositoryJPA nodeRepositoryJPA;

    @Mock
    private EdgeRepositoryJPA edgeRepositoryJPA;

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @InjectMocks
    private VisualizationService visualizationService;

    private UUID problemId;
    private UUID algorithmId;
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        problemId = UUID.randomUUID();
        algorithmId = UUID.randomUUID();
        
        algorithm = Algorithm.builder()
            .id(algorithmId)
            .name("TestAlgorithm")
            .color(Color.RED)
            .build();
        
        // Mock createNodeUseCase to return a proper ResponseNodeDTO
        ResponseNodeDTO mockNodeDTO = ResponseNodeDTO.builder()
            .id(UUID.randomUUID())
            .type(NodeType.DEFAULT)
            .position(Position3DDTO.builder().x(1.0).y(2.0).z(0.0).build())
            .algorithmId(algorithmId)
            .build();
        lenient().when(createNodeUseCase.execute(any(), any())).thenReturn(mockNodeDTO);
    }

    @Test
    void testProcessAndPersistVisualization_withNullPath_shouldReturn() throws Exception {
        // When
        visualizationService.processAndPersistVisualization(null, problemId, false);

        // Then - no exception should be thrown
        verify(createNodeUseCase, never()).execute(any(), any());
    }

    @Test
    void testProcessAndPersistVisualization_withNonExistentPath_shouldReturn() throws Exception {
        // Given
        Path nonExistentPath = Path.of("/tmp/non-existent-file.json");

        // When
        visualizationService.processAndPersistVisualization(nonExistentPath, problemId, false);

        // Then - no exception should be thrown
        verify(createNodeUseCase, never()).execute(any(), any());
    }

    @Test
    void testProcessAndPersistVisualization_withValidJson_shouldPersistNodes() throws Exception {
        // Given
        String jsonContent = """
        {
            "algorithms": ["TestAlgorithm"],
            "algorithmColors": ["#FF0000"],
            "nodes": [
                {
                    "id": "node1",
                    "type": "start",
                    "size": 2.5,
                    "color": "#FFD700",
                    "fitness": 100.0,
                    "algorithm": "TestAlgorithm",
                    "x_fr": 10.5,
                    "y_fr": 20.3,
                    "x_kk": 15.2,
                    "y_kk": 25.1
                },
                {
                    "id": "node2",
                    "type": "end",
                    "size": 1.5,
                    "color": "#C0C0C0",
                    "fitness": 50.0,
                    "algorithm": "TestAlgorithm",
                    "x_fr": 30.5,
                    "y_fr": 40.3,
                    "x_kk": 35.2,
                    "y_kk": 45.1
                }
            ],
            "edges": [],
            "stats": {
                "node_count": 2,
                "edge_count": 0,
                "component_count": 1
            }
        }
        """;

        Path tempFile = Files.createTempFile("test-viz", ".json");
        Files.writeString(tempFile, jsonContent);

        VisualizationData vizData = new ObjectMapper().readValue(jsonContent, VisualizationData.class);
        when(objectMapper.readValue(anyString(), eq(VisualizationData.class))).thenReturn(vizData);
        when(algorithmRepositoryJPA.findByProblemId(problemId)).thenReturn(Arrays.asList(algorithm));

        try {
            // When
            visualizationService.processAndPersistVisualization(tempFile, problemId, false);

            // Then
            verify(createNodeUseCase, times(2)).execute(any(), eq(algorithmId));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testMapNodeType_withValidTypes() throws Exception {
        // This test verifies the internal mapping logic through integration
        String jsonContent = """
        {
            "algorithms": ["TestAlgorithm"],
            "nodes": [
                {
                    "id": "node1",
                    "type": "START",
                    "algorithm": "TestAlgorithm",
                    "x_fr": 10.5,
                    "y_fr": 20.3,
                    "x_kk": 15.2,
                    "y_kk": 25.1
                },
                {
                    "id": "node2",
                    "type": "END",
                    "algorithm": "TestAlgorithm",
                    "x_fr": 30.5,
                    "y_fr": 40.3,
                    "x_kk": 35.2,
                    "y_kk": 45.1
                },
                {
                    "id": "node3",
                    "type": "default",
                    "algorithm": "TestAlgorithm",
                    "x_fr": 50.5,
                    "y_fr": 60.3,
                    "x_kk": 55.2,
                    "y_kk": 65.1
                }
            ],
            "edges": [],
            "stats": {
                "node_count": 3,
                "edge_count": 0,
                "component_count": 1
            }
        }
        """;

        Path tempFile = Files.createTempFile("test-viz-types", ".json");
        Files.writeString(tempFile, jsonContent);

        VisualizationData vizData = new ObjectMapper().readValue(jsonContent, VisualizationData.class);
        when(objectMapper.readValue(anyString(), eq(VisualizationData.class))).thenReturn(vizData);
        when(algorithmRepositoryJPA.findByProblemId(problemId)).thenReturn(Arrays.asList(algorithm));

        try {
            // When
            visualizationService.processAndPersistVisualization(tempFile, problemId, false);

            // Then - verify all three nodes are processed
            verify(createNodeUseCase, times(3)).execute(any(), eq(algorithmId));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
