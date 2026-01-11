package com.tfg.backend.algorithm.application.mapper;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.RegistrationNodeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.domain.NodeType;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NodeMapperTest {

    @Mock
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @InjectMocks
    private NodeMapper nodeMapper;

    private UUID algorithmId;
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithmId = UUID.randomUUID();
        algorithm = mock(Algorithm.class);
        when(algorithm.getId()).thenReturn(algorithmId);
    }

    @Test
    void fromDTOtoObject_validDTO_shouldMapCorrectly() {
        // Arrange
        RegistrationNodeDTO registrationDTO = AlgorithmMother.validRegistrationNodeDTO(NodeType.START);
        when(algorithmRepositoryJPA.findById(algorithmId)).thenReturn(Optional.of(algorithm));

        // Act
        Node result = nodeMapper.fromDTOtoObject(registrationDTO, algorithmId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo(registrationDTO.getType());
        assertThat(result.getPosition()).isNotNull();
        assertThat(result.getPosition().getX()).isEqualTo(registrationDTO.getPosition().getX());
        assertThat(result.getPosition().getY()).isEqualTo(registrationDTO.getPosition().getY());
        assertThat(result.getPosition().getZ()).isEqualTo(registrationDTO.getPosition().getZ());
        assertThat(result.getAlgorithm()).isEqualTo(algorithm);
        verify(algorithmRepositoryJPA).findById(algorithmId);
    }

    @Test
    void fromObjectToDTO_validNode_shouldMapCorrectly() {
        // Arrange
        Node node = AlgorithmMother.validNode();

        // Act
        ResponseNodeDTO result = nodeMapper.fromObjectToDTO(node);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(node.getId());
        assertThat(result.getType()).isEqualTo(node.getType());
        assertThat(result.getPosition()).isNotNull();
        assertThat(result.getPosition().getX()).isEqualTo(node.getPosition().getX());
        assertThat(result.getPosition().getY()).isEqualTo(node.getPosition().getY());
        assertThat(result.getPosition().getZ()).isEqualTo(node.getPosition().getZ());
        assertThat(result.getAlgorithmId()).isEqualTo(node.getAlgorithm().getId());
    }
}
