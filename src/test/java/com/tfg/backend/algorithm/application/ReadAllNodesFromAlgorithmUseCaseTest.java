package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.application.mapper.NodeMapper;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadAllNodesFromAlgorithmUseCaseTest {

    @Mock
    private NodeMapper nodeMapper;

    @Mock
    private NodeRepositoryJPA nodeRepositoryJPA;

    @InjectMocks
    private ReadAllNodesFromAlgorithmUseCase readAllNodesFromAlgorithmUseCase;

    private UUID algorithmId;
    private List<Node> nodes;
    private List<ResponseNodeDTO> responseDTOs;

    @BeforeEach
    void setUp() {
        algorithmId = UUID.randomUUID();
        
        Node node1 = mock(Node.class);
        Node node2 = mock(Node.class);
        Node node3 = mock(Node.class);
        nodes = Arrays.asList(node1, node2, node3);

        ResponseNodeDTO responseDTO1 = mock(ResponseNodeDTO.class);
        ResponseNodeDTO responseDTO2 = mock(ResponseNodeDTO.class);
        ResponseNodeDTO responseDTO3 = mock(ResponseNodeDTO.class);
        responseDTOs = Arrays.asList(responseDTO1, responseDTO2, responseDTO3);
    }

    @Test
    void execute_validAlgorithmId_shouldReturnAllNodes() {
        // Arrange
        when(nodeRepositoryJPA.findByAlgorithmId(algorithmId)).thenReturn(nodes);
        when(nodeMapper.fromObjectToDTO(nodes.get(0))).thenReturn(responseDTOs.get(0));
        when(nodeMapper.fromObjectToDTO(nodes.get(1))).thenReturn(responseDTOs.get(1));
        when(nodeMapper.fromObjectToDTO(nodes.get(2))).thenReturn(responseDTOs.get(2));

        // Act
        List<ResponseNodeDTO> result = readAllNodesFromAlgorithmUseCase.execute(algorithmId);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(responseDTOs);
        verify(nodeRepositoryJPA).findByAlgorithmId(algorithmId);
        verify(nodeMapper, times(3)).fromObjectToDTO(any(Node.class));
    }

    @Test
    void execute_algorithmWithNoNodes_shouldReturnEmptyList() {
        // Arrange
        when(nodeRepositoryJPA.findByAlgorithmId(algorithmId)).thenReturn(Arrays.asList());

        // Act
        List<ResponseNodeDTO> result = readAllNodesFromAlgorithmUseCase.execute(algorithmId);

        // Assert
        assertThat(result).isEmpty();
        verify(nodeRepositoryJPA).findByAlgorithmId(algorithmId);
        verify(nodeMapper, never()).fromObjectToDTO(any());
    }
}
