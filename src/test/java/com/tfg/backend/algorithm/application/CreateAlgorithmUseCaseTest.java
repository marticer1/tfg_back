package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.RegistrationAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.exceptions.AlgorithmAlreadyExistsException;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
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
class CreateAlgorithmUseCaseTest {

    @Mock
    private AlgorithmMapper algorithmMapper;

    @Mock
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @Mock
    private CreateNodeUseCase createNodeUseCase;

    @InjectMocks
    private CreateAlgorithmUseCase createAlgorithmUseCase;

    private RegistrationAlgorithmDTO registrationDTO;
    private Algorithm algorithm;
    private ResponseAlgorithmDTO responseDTO;
    private UUID algorithmId;
    private UUID problemId;

    @BeforeEach
    void setUp() {
        registrationDTO = AlgorithmMother.validRegistrationAlgorithmDTO();
        algorithm = mock(Algorithm.class);
        responseDTO = mock(ResponseAlgorithmDTO.class);
        algorithmId = UUID.randomUUID();
        problemId = UUID.randomUUID();

        when(algorithm.getId()).thenReturn(algorithmId);
    }

    @Test
    void execute_validDTO_shouldCreateAndReturnResponse() {
        // Arrange
        when(algorithmMapper.fromDTOtoObject(registrationDTO, problemId)).thenReturn(algorithm);
        when(algorithmRepositoryJPA.existsById(algorithmId)).thenReturn(false);
        when(algorithmRepositoryJPA.save(algorithm)).thenReturn(algorithm);
        when(algorithmMapper.fromObjectToDTO(algorithm)).thenReturn(responseDTO);

        // Act
        ResponseAlgorithmDTO result = createAlgorithmUseCase.execute(registrationDTO, problemId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(algorithmMapper).fromDTOtoObject(registrationDTO, problemId);
        verify(algorithmRepositoryJPA).existsById(algorithmId);
        verify(algorithmRepositoryJPA).save(algorithm);
        verify(algorithmMapper).fromObjectToDTO(algorithm);
        verify(createNodeUseCase, times(registrationDTO.getNodes().size())).execute(any(), eq(algorithmId));
    }

    @Test
    void execute_whenAlgorithmIdAlreadyExists_shouldThrowAlgorithmAlreadyExistsException() {
        // Arrange
        when(algorithmMapper.fromDTOtoObject(registrationDTO, problemId)).thenReturn(algorithm);
        when(algorithmRepositoryJPA.existsById(algorithmId)).thenReturn(true);

        // Act & Assert
        assertThrows(
                AlgorithmAlreadyExistsException.class,
                () -> createAlgorithmUseCase.execute(registrationDTO, problemId)
        );

        verify(algorithmMapper).fromDTOtoObject(registrationDTO, problemId);
        verify(algorithmRepositoryJPA).existsById(algorithmId);
        verify(algorithmRepositoryJPA, never()).save(any());
        verify(algorithmMapper, never()).fromObjectToDTO(any());
        verify(createNodeUseCase, never()).execute(any(), any());
    }

    @Test
    void execute_dtoWithoutNodes_shouldCreateWithoutNodes() {
        // Arrange
        RegistrationAlgorithmDTO dtoWithoutNodes = AlgorithmMother.validRegistrationAlgorithmDTOWithoutFile();
        when(algorithmMapper.fromDTOtoObject(dtoWithoutNodes, problemId)).thenReturn(algorithm);
        when(algorithmRepositoryJPA.existsById(algorithmId)).thenReturn(false);
        when(algorithmRepositoryJPA.save(algorithm)).thenReturn(algorithm);
        when(algorithmMapper.fromObjectToDTO(algorithm)).thenReturn(responseDTO);

        // Act
        ResponseAlgorithmDTO result = createAlgorithmUseCase.execute(dtoWithoutNodes, problemId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(createNodeUseCase, never()).execute(any(), any());
    }
}
