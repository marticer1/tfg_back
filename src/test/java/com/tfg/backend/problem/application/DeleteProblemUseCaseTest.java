package com.tfg.backend.problem.application;

import com.tfg.backend.problem.domain.exceptions.ProblemNotFoundException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProblemUseCaseTest {

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @InjectMocks
    private DeleteProblemUseCase deleteProblemUseCase;

    private UUID problemId;

    @BeforeEach
    void setUp() {
        problemId = UUID.randomUUID();
    }

    @Test
    void execute_existingProblem_shouldDeleteProblem() {
        // Arrange
        when(problemRepositoryJPA.existsById(problemId)).thenReturn(true);

        // Act
        deleteProblemUseCase.execute(problemId);

        // Assert
        verify(problemRepositoryJPA).existsById(problemId);
        verify(problemRepositoryJPA).deleteById(problemId);
    }

    @Test
    void execute_nonExistingProblem_shouldThrowProblemNotFoundException() {
        // Arrange
        when(problemRepositoryJPA.existsById(problemId)).thenReturn(false);

        // Act & Assert
        assertThrows(
                ProblemNotFoundException.class,
                () -> deleteProblemUseCase.execute(problemId)
        );

        verify(problemRepositoryJPA).existsById(problemId);
        verify(problemRepositoryJPA, never()).deleteById(any());
    }
}
