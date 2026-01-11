package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.RegistrationNodeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.application.mapper.NodeMapper;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.domain.NodeType;
import com.tfg.backend.algorithm.domain.exceptions.NodeAlreadyExistsException;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNodeUseCaseTest {

    @Mock
    private NodeMapper nodeMapper;

    @Mock
    private NodeRepositoryJPA nodeRepositoryJPA;

    @InjectMocks
    private CreateNodeUseCase createNodeUseCase;

    private RegistrationNodeDTO registrationDTO;
    private Node node;
    private ResponseNodeDTO responseDTO;
    private UUID nodeId;
    private UUID algorithmId;

    @BeforeEach
    void setUp() {
        registrationDTO = AlgorithmMother.validRegistrationNodeDTO(NodeType.START);
        node = mock(Node.class);
        responseDTO = mock(ResponseNodeDTO.class);
        nodeId = UUID.randomUUID();
        algorithmId = UUID.randomUUID();

        when(node.getId()).thenReturn(nodeId);
    }

    @Test
    void execute_validDTO_shouldCreateAndReturnResponse() {
        // Arrange
        when(nodeMapper.fromDTOtoObject(registrationDTO, algorithmId)).thenReturn(node);
        when(nodeRepositoryJPA.existsById(nodeId)).thenReturn(false);
        when(nodeRepositoryJPA.save(node)).thenReturn(node);
        when(nodeMapper.fromObjectToDTO(node)).thenReturn(responseDTO);

        // Act
        ResponseNodeDTO result = createNodeUseCase.execute(registrationDTO, algorithmId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(nodeMapper).fromDTOtoObject(registrationDTO, algorithmId);
        verify(nodeRepositoryJPA).existsById(nodeId);
        verify(nodeRepositoryJPA).save(node);
        verify(nodeMapper).fromObjectToDTO(node);
    }

    @Test
    void execute_whenNodeIdAlreadyExists_shouldThrowNodeAlreadyExistsException() {
        // Arrange
        when(nodeMapper.fromDTOtoObject(registrationDTO, algorithmId)).thenReturn(node);
        when(nodeRepositoryJPA.existsById(nodeId)).thenReturn(true);

        // Act & Assert
        assertThrows(
                NodeAlreadyExistsException.class,
                () -> createNodeUseCase.execute(registrationDTO, algorithmId)
        );

        verify(nodeMapper).fromDTOtoObject(registrationDTO, algorithmId);
        verify(nodeRepositoryJPA).existsById(nodeId);
        verify(nodeRepositoryJPA, never()).save(any());
        verify(nodeMapper, never()).fromObjectToDTO(any());
    }
}
