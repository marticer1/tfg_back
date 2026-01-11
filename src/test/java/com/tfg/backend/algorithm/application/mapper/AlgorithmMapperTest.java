package com.tfg.backend.algorithm.application.mapper;

import com.tfg.backend.algorithm.AlgorithmMother;
import com.tfg.backend.algorithm.application.dto.RegistrationAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.UpdateAlgorithmDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.awt.Color;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlgorithmMapperTest {

    @Mock
    private ProblemRepositoryJPA problemRepositoryJPA;

    @InjectMocks
    private AlgorithmMapper algorithmMapper;

    private UUID problemId;
    private Problem problem;

    @BeforeEach
    void setUp() {
        problemId = UUID.randomUUID();
        problem = mock(Problem.class);
        when(problem.getId()).thenReturn(problemId);
    }

    @Test
    void fromDTOtoObject_validDTO_shouldMapCorrectly() {
        // Arrange
        RegistrationAlgorithmDTO registrationDTO = AlgorithmMother.validRegistrationAlgorithmDTO();
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.of(problem));

        // Act
        Algorithm result = algorithmMapper.fromDTOtoObject(registrationDTO, problemId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(registrationDTO.getName());
        assertThat(result.getColor()).isEqualTo(registrationDTO.getColor());
        assertThat(result.getProblem()).isEqualTo(problem);
        assertThat(result.getFile()).isNotNull();
        assertThat(result.getFile().getFileName()).isEqualTo(registrationDTO.getFile().getFileName());
        verify(problemRepositoryJPA).findById(problemId);
    }

    @Test
    void fromDTOtoObject_dtoWithoutFile_shouldMapCorrectly() {
        // Arrange
        RegistrationAlgorithmDTO dtoWithoutFile = AlgorithmMother.validRegistrationAlgorithmDTOWithoutFile();
        when(problemRepositoryJPA.findById(problemId)).thenReturn(Optional.of(problem));

        // Act
        Algorithm result = algorithmMapper.fromDTOtoObject(dtoWithoutFile, problemId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFile()).isNull();
        verify(problemRepositoryJPA).findById(problemId);
    }

    @Test
    void fromObjectToDTO_validAlgorithm_shouldMapCorrectly() {
        // Arrange
        Algorithm algorithm = AlgorithmMother.validAlgorithm();

        // Act
        ResponseAlgorithmDTO result = algorithmMapper.fromObjectToDTO(algorithm);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(algorithm.getId());
        assertThat(result.getName()).isEqualTo(algorithm.getName());
        assertThat(result.getColor()).isEqualTo(algorithm.getColor());
        assertThat(result.getProblemId()).isEqualTo(algorithm.getProblem().getId());
        assertThat(result.getFile()).isNotNull();
        assertThat(result.getFile().getId()).isEqualTo(algorithm.getFile().getId());
    }

    @Test
    void fromObjectToDTO_algorithmWithoutFile_shouldMapCorrectly() {
        // Arrange
        Algorithm algorithm = AlgorithmMother.validAlgorithmWithoutFile();

        // Act
        ResponseAlgorithmDTO result = algorithmMapper.fromObjectToDTO(algorithm);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFile()).isNull();
    }

    @Test
    void updateFromDTO_validDTO_shouldUpdateAlgorithm() {
        // Arrange
        Algorithm algorithm = AlgorithmMother.validAlgorithm();
        UpdateAlgorithmDTO updateDTO = AlgorithmMother.validUpdateAlgorithmDTO();

        // Act
        algorithmMapper.updateFromDTO(algorithm, updateDTO);

        // Assert
        assertThat(algorithm.getName()).isEqualTo(updateDTO.getName());
        assertThat(algorithm.getColor()).isEqualTo(updateDTO.getColor());
    }
}
