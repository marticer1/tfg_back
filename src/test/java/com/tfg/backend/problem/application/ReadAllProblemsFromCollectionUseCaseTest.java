package com.tfg.backend.problem.application;

import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
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
class ReadAllProblemsFromCollectionUseCaseTest {

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @InjectMocks
    private ReadAllProblemsFromCollectionUseCase readAllProblemsFromCollectionUseCase;

    private UUID collectionId;
    private Problem problem1;
    private Problem problem2;
    private ResponseProblemDTO responseDTO1;
    private ResponseProblemDTO responseDTO2;

    @BeforeEach
    void setUp() {
        collectionId = UUID.randomUUID();
        problem1 = mock(Problem.class);
        problem2 = mock(Problem.class);
        responseDTO1 = mock(ResponseProblemDTO.class);
        responseDTO2 = mock(ResponseProblemDTO.class);
    }

    @Test
    void execute_shouldReturnAllProblemsFromCollection() {
        // Arrange
        List<Problem> problems = Arrays.asList(problem1, problem2);
        when(problemRepositoryJPA.findByProblemCollectionId(collectionId)).thenReturn(problems);
        when(problemMapper.fromObjectToDTO(problem1)).thenReturn(responseDTO1);
        when(problemMapper.fromObjectToDTO(problem2)).thenReturn(responseDTO2);

        // Act
        List<ResponseProblemDTO> result = readAllProblemsFromCollectionUseCase.execute(collectionId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDTO1, responseDTO2);
        verify(problemRepositoryJPA).findByProblemCollectionId(collectionId);
        verify(problemMapper).fromObjectToDTO(problem1);
        verify(problemMapper).fromObjectToDTO(problem2);
    }

    @Test
    void execute_emptyCollection_shouldReturnEmptyList() {
        // Arrange
        when(problemRepositoryJPA.findByProblemCollectionId(collectionId)).thenReturn(List.of());

        // Act
        List<ResponseProblemDTO> result = readAllProblemsFromCollectionUseCase.execute(collectionId);

        // Assert
        assertThat(result).isEmpty();
        verify(problemRepositoryJPA).findByProblemCollectionId(collectionId);
        verify(problemMapper, never()).fromObjectToDTO(any());
    }
}
