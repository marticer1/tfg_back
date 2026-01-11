package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
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
class ReadAllAlgorithmsFromProblemUseCaseTest {

    @Mock
    private AlgorithmMapper algorithmMapper;

    @Mock
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @InjectMocks
    private ReadAllAlgorithmsFromProblemUseCase readAllAlgorithmsFromProblemUseCase;

    private UUID problemId;
    private List<Algorithm> algorithms;
    private List<ResponseAlgorithmDTO> responseDTOs;

    @BeforeEach
    void setUp() {
        problemId = UUID.randomUUID();
        
        Algorithm algorithm1 = mock(Algorithm.class);
        Algorithm algorithm2 = mock(Algorithm.class);
        algorithms = Arrays.asList(algorithm1, algorithm2);

        ResponseAlgorithmDTO responseDTO1 = mock(ResponseAlgorithmDTO.class);
        ResponseAlgorithmDTO responseDTO2 = mock(ResponseAlgorithmDTO.class);
        responseDTOs = Arrays.asList(responseDTO1, responseDTO2);
    }

    @Test
    void execute_validProblemId_shouldReturnAllAlgorithms() {
        // Arrange
        when(algorithmRepositoryJPA.findByProblemId(problemId)).thenReturn(algorithms);
        when(algorithmMapper.fromObjectToDTO(algorithms.get(0))).thenReturn(responseDTOs.get(0));
        when(algorithmMapper.fromObjectToDTO(algorithms.get(1))).thenReturn(responseDTOs.get(1));

        // Act
        List<ResponseAlgorithmDTO> result = readAllAlgorithmsFromProblemUseCase.execute(problemId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(responseDTOs);
        verify(algorithmRepositoryJPA).findByProblemId(problemId);
        verify(algorithmMapper, times(2)).fromObjectToDTO(any(Algorithm.class));
    }

    @Test
    void execute_problemWithNoAlgorithms_shouldReturnEmptyList() {
        // Arrange
        when(algorithmRepositoryJPA.findByProblemId(problemId)).thenReturn(Arrays.asList());

        // Act
        List<ResponseAlgorithmDTO> result = readAllAlgorithmsFromProblemUseCase.execute(problemId);

        // Assert
        assertThat(result).isEmpty();
        verify(algorithmRepositoryJPA).findByProblemId(problemId);
        verify(algorithmMapper, never()).fromObjectToDTO(any());
    }
}
