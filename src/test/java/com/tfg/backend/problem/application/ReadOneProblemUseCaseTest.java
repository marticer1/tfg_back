package com.tfg.backend.problem.application;

import com.tfg.backend.algorithm.application.ReadAllAlgorithmsFromProblemUseCase;
import com.tfg.backend.algorithm.application.ReadAllEdgesFromAlgorithmUseCase;
import com.tfg.backend.algorithm.application.ReadAllNodesFromAlgorithmUseCase;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.ResponseEdgeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.exceptions.ProblemNotFoundException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadOneProblemUseCaseTest {

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @Mock
    private ReadAllAlgorithmsFromProblemUseCase readAllAlgorithmsFromProblemUseCase;

    @Mock
    private ReadAllNodesFromAlgorithmUseCase readAllNodesFromAlgorithmUseCase;

    @Mock
    private ReadAllEdgesFromAlgorithmUseCase readAllEdgesFromAlgorithmUseCase;

    @InjectMocks
    private ReadOneProblemUseCase readOneProblemUseCase;

    private UUID problemId;
    private Problem problem;
    private ResponseProblemDTO responseDTO;
    private List<ResponseAlgorithmDTO> algorithms;

    @BeforeEach
    void setUp() {
        problemId = UUID.randomUUID();
        problem = mock(Problem.class);
        responseDTO = mock(ResponseProblemDTO.class);
        
        ResponseAlgorithmDTO algorithm1 = mock(ResponseAlgorithmDTO.class);
        ResponseAlgorithmDTO algorithm2 = mock(ResponseAlgorithmDTO.class);
        lenient().when(algorithm1.getId()).thenReturn(UUID.randomUUID());
        lenient().when(algorithm2.getId()).thenReturn(UUID.randomUUID());
        algorithms = Arrays.asList(algorithm1, algorithm2);
    }

    @Test
    void execute_existingProblem_shouldReturnProblemWithAlgorithms() {
        // Arrange
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.of(problem));
        when(problemMapper.fromObjectToDTO(problem)).thenReturn(responseDTO);
        when(readAllAlgorithmsFromProblemUseCase.execute(problemId)).thenReturn(algorithms);
        when(readAllNodesFromAlgorithmUseCase.execute(any())).thenReturn(Collections.emptyList());
        when(readAllEdgesFromAlgorithmUseCase.execute(any())).thenReturn(Collections.emptyList());

        // Act
        ResponseProblemDTO result = readOneProblemUseCase.execute(problemId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(problemRepositoryJPA).findById(problemId);
        verify(problemMapper).fromObjectToDTO(problem);
        verify(readAllAlgorithmsFromProblemUseCase).execute(problemId);
        verify(responseDTO).setAlgorithms(algorithms);
        verify(readAllNodesFromAlgorithmUseCase, times(algorithms.size())).execute(any());
        verify(readAllEdgesFromAlgorithmUseCase, times(algorithms.size())).execute(any());
    }

    @Test
    void execute_nonExistingProblem_shouldThrowProblemNotFoundException() {
        // Arrange
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ProblemNotFoundException.class,
                () -> readOneProblemUseCase.execute(problemId)
        );

        verify(problemRepositoryJPA).findById(problemId);
        verify(problemMapper, never()).fromObjectToDTO(any());
        verify(readAllAlgorithmsFromProblemUseCase, never()).execute(any());
    }
}
