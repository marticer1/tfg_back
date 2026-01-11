package com.tfg.backend.problem.application;

import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.dto.UpdateProblemDTO;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProblemUseCaseTest {

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @InjectMocks
    private UpdateProblemUseCase updateProblemUseCase;

    private UpdateProblemDTO updateDTO;
    private Problem problem;
    private ResponseProblemDTO responseDTO;
    private UUID problemId;

    @BeforeEach
    void setUp() {
        // Arrange
        updateDTO = mock(UpdateProblemDTO.class);
        problem = mock(Problem.class);
        responseDTO = mock(ResponseProblemDTO.class);
        problemId = UUID.randomUUID();
    }

    @Test
    void execute_validIdAndDTO_shouldUpdateAndReturnResponse() {
        // Arrange
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.of(problem));
        when(problemRepositoryJPA.save(problem)).thenReturn(problem);
        when(problemMapper.fromObjectToDTO(problem)).thenReturn(responseDTO);

        // Act
        ResponseProblemDTO result = updateProblemUseCase.execute(problemId, updateDTO);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(problemRepositoryJPA).findById(problemId);
        verify(problemMapper).updateFromDTO(problem, updateDTO);
        verify(problemRepositoryJPA).save(problem);
        verify(problemMapper).fromObjectToDTO(problem);
    }

    @Test
    void execute_whenProblemIdDoesNotExist_shouldThrowProblemNotFoundException() {
        // Arrange
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ProblemNotFoundException.class,
                () -> updateProblemUseCase.execute(problemId, updateDTO)
        );

        verify(problemRepositoryJPA).findById(problemId);
        verify(problemMapper, never()).updateFromDTO(any(), any());
        verify(problemRepositoryJPA, never()).save(any());
        verify(problemMapper, never()).fromObjectToDTO(any());
    }
}
