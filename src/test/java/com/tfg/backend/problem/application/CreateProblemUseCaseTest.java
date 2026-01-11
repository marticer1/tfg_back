package com.tfg.backend.problem.application;

import com.tfg.backend.algorithm.application.CreateAlgorithmUseCase;
import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.exceptions.ProblemAlreadyExistsException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
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
class CreateProblemUseCaseTest {

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @Mock
    private CreateAlgorithmUseCase createAlgorithmUseCase;

    @InjectMocks
    private CreateProblemUseCase createProblemUseCase;

    private RegistrationProblemDTO registrationDTO;
    private Problem problem;
    private ResponseProblemDTO responseDTO;
    private UUID problemId;
    private UUID collectionId;

    @BeforeEach
    void setUp() {
        registrationDTO = mock(RegistrationProblemDTO.class);
        problem = mock(Problem.class);
        responseDTO = mock(ResponseProblemDTO.class);
        problemId = UUID.randomUUID();
        collectionId = UUID.randomUUID();

        when(problem.getId()).thenReturn(problemId);
    }

    @Test
    void execute_validDTO_shouldCreateAndReturnResponse() {
        // Arrange
        when(problemMapper.fromDTOtoObject(registrationDTO, collectionId)).thenReturn(problem);
        when(problemRepositoryJPA.existsById(problemId)).thenReturn(false);
        when(problemRepositoryJPA.save(problem)).thenReturn(problem);
        when(problemMapper.fromObjectToDTO(problem)).thenReturn(responseDTO);

        // Act
        ResponseProblemDTO result = createProblemUseCase.execute(registrationDTO, collectionId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(problemMapper).fromDTOtoObject(registrationDTO, collectionId);
        verify(problemRepositoryJPA).existsById(problemId);
        verify(problemRepositoryJPA).save(problem);
        verify(problemMapper).fromObjectToDTO(problem);
    }

    @Test
    void execute_whenProblemIdAlreadyExists_shouldThrowProblemAlreadyExistsException() {
        // Arrange
        when(problemMapper.fromDTOtoObject(registrationDTO, collectionId)).thenReturn(problem);
        when(problemRepositoryJPA.existsById(problemId)).thenReturn(true);

        // Act & Assert
        assertThrows(
                ProblemAlreadyExistsException.class,
                () -> createProblemUseCase.execute(registrationDTO, collectionId)
        );

        verify(problemMapper).fromDTOtoObject(registrationDTO, collectionId);
        verify(problemRepositoryJPA).existsById(problemId);
        verify(problemRepositoryJPA, never()).save(any());
        verify(problemMapper, never()).fromObjectToDTO(any());
    }
}
