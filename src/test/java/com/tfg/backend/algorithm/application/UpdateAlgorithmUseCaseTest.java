package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.UpdateAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.exceptions.AlgorithmNotFoundException;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAlgorithmUseCaseTest {

    @Mock
    private AlgorithmMapper algorithmMapper;

    @Mock
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @InjectMocks
    private UpdateAlgorithmUseCase updateAlgorithmUseCase;

    private UUID algorithmId;
    private Algorithm algorithm;
    private UpdateAlgorithmDTO updateDTO;
    private ResponseAlgorithmDTO responseDTO;

    @BeforeEach
    void setUp() {
        algorithmId = UUID.randomUUID();
        algorithm = mock(Algorithm.class);
        updateDTO = AlgorithmMother.validUpdateAlgorithmDTO();
        responseDTO = mock(ResponseAlgorithmDTO.class);
    }

    @Test
    void execute_validDTO_shouldUpdateAndReturnResponse() {
        // Arrange
        when(algorithmRepositoryJPA.findById(algorithmId)).thenReturn(Optional.of(algorithm));
        when(algorithmRepositoryJPA.save(algorithm)).thenReturn(algorithm);
        when(algorithmMapper.fromObjectToDTO(algorithm)).thenReturn(responseDTO);

        // Act
        ResponseAlgorithmDTO result = updateAlgorithmUseCase.execute(algorithmId, updateDTO);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(algorithmRepositoryJPA).findById(algorithmId);
        verify(algorithmMapper).updateFromDTO(algorithm, updateDTO);
        verify(algorithmRepositoryJPA).save(algorithm);
        verify(algorithmMapper).fromObjectToDTO(algorithm);
    }

    @Test
    void execute_algorithmNotFound_shouldThrowAlgorithmNotFoundException() {
        // Arrange
        when(algorithmRepositoryJPA.findById(algorithmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                AlgorithmNotFoundException.class,
                () -> updateAlgorithmUseCase.execute(algorithmId, updateDTO)
        );

        verify(algorithmRepositoryJPA).findById(algorithmId);
        verify(algorithmMapper, never()).updateFromDTO(any(), any());
        verify(algorithmRepositoryJPA, never()).save(any());
        verify(algorithmMapper, never()).fromObjectToDTO(any());
    }
}
