package com.tfg.backend.problem.application.mapper;

import com.tfg.backend.problem.ProblemMother;
import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.dto.UpdateProblemDTO;
import com.tfg.backend.problem.domain.ContinuousProblem;
import com.tfg.backend.problem.domain.DiscreteProblem;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.Shape;
import com.tfg.backend.problemCollection.ProblemCollectionMother;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemMapperTest {

    @Mock
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @InjectMocks
    private ProblemMapper problemMapper;

    @Test
    void fromDTOtoObject_discreteProblem_shouldMapCorrectly() {
        // Arrange
        RegistrationProblemDTO dto = ProblemMother.validRegistrationDiscreteProblemDTO();
        UUID collectionId = UUID.randomUUID();
        ProblemCollection problemCollection = ProblemCollectionMother.validProblemCollection();
        when(problemCollectionRepositoryJPA.findById(collectionId))
                .thenReturn(Optional.of(problemCollection));

        // Act
        Problem result = problemMapper.fromDTOtoObject(dto, collectionId);

        // Assert
        assertThat(result).isInstanceOf(DiscreteProblem.class);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getValueBestKnownSolution()).isEqualTo(dto.getValueBestKnownSolution());
        assertThat(result.getNumberRuns()).isEqualTo(dto.getNumberRuns());
        assertThat(result.getVertexSize()).isEqualTo(dto.getVertexSize());
        assertThat(result.getArrowSize()).isEqualTo(dto.getArrowSize());
        assertThat(result.isTreeLayout()).isEqualTo(dto.getTreeLayout());
        assertThat(result.getProblemCollection()).isEqualTo(problemCollection);
        assertThat(result.getStandardPartitioning()).isNotNull();
        assertThat(result.getStandardPartitioning().getHypercube()).isEqualTo(dto.getStandardPartitioning().getHypercube());
        // Verify new color and shape fields
        assertThat(result.getColorStart()).isEqualTo(dto.getColorStart());
        assertThat(result.getColorEnd()).isEqualTo(dto.getColorEnd());
        assertThat(result.getStartShape()).isEqualTo(dto.getStartShape());
        assertThat(result.getEndShape()).isEqualTo(dto.getEndShape());
        assertThat(result.getDefaultShape()).isEqualTo(dto.getDefaultShape());
    }

    @Test
    void fromDTOtoObject_continuousProblem_shouldMapCorrectly() {
        // Arrange
        RegistrationProblemDTO dto = ProblemMother.validRegistrationContinuousProblemDTO();
        UUID collectionId = UUID.randomUUID();
        ProblemCollection problemCollection = ProblemCollectionMother.validProblemCollection();
        when(problemCollectionRepositoryJPA.findById(collectionId))
                .thenReturn(Optional.of(problemCollection));

        // Act
        Problem result = problemMapper.fromDTOtoObject(dto, collectionId);

        // Assert
        assertThat(result).isInstanceOf(ContinuousProblem.class);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getValueBestKnownSolution()).isEqualTo(dto.getValueBestKnownSolution());
        assertThat(result.getAgglomerativeClustering()).isNotNull();
        assertThat(result.getAgglomerativeClustering().getClusterSize()).isEqualTo(dto.getAgglomerativeClustering().getClusterSize());
        // Verify new color and shape fields
        assertThat(result.getColorStart()).isEqualTo(dto.getColorStart());
        assertThat(result.getColorEnd()).isEqualTo(dto.getColorEnd());
        assertThat(result.getStartShape()).isEqualTo(dto.getStartShape());
        assertThat(result.getEndShape()).isEqualTo(dto.getEndShape());
        assertThat(result.getDefaultShape()).isEqualTo(dto.getDefaultShape());
    }

    @Test
    void fromObjectToDTO_discreteProblem_shouldMapCorrectly() {
        // Arrange
        DiscreteProblem problem = ProblemMother.validDiscreteProblem();

        // Act
        ResponseProblemDTO result = problemMapper.fromObjectToDTO(problem);

        // Assert
        assertThat(result.getId()).isEqualTo(problem.getId());
        assertThat(result.getProblemType()).isEqualTo("DiscreteProblem");
        assertThat(result.getValueBestKnownSolution()).isEqualTo(problem.getValueBestKnownSolution());
        assertThat(result.getNumberRuns()).isEqualTo(problem.getNumberRuns());
        assertThat(result.getVertexSize()).isEqualTo(problem.getVertexSize());
        assertThat(result.getArrowSize()).isEqualTo(problem.getArrowSize());
        assertThat(result.isTreeLayout()).isEqualTo(problem.isTreeLayout());
        assertThat(result.getProblemCollectionId()).isEqualTo(problem.getProblemCollection().getId());
        assertThat(result.getStandardPartitioning()).isNotNull();
        // Verify new color and shape fields
        assertThat(result.getColorStart()).isEqualTo(problem.getColorStart());
        assertThat(result.getColorEnd()).isEqualTo(problem.getColorEnd());
        assertThat(result.getStartShape()).isEqualTo(problem.getStartShape());
        assertThat(result.getEndShape()).isEqualTo(problem.getEndShape());
        assertThat(result.getDefaultShape()).isEqualTo(problem.getDefaultShape());
    }

    @Test
    void fromObjectToDTO_continuousProblem_shouldMapCorrectly() {
        // Arrange
        ContinuousProblem problem = ProblemMother.validContinuousProblem();

        // Act
        ResponseProblemDTO result = problemMapper.fromObjectToDTO(problem);

        // Assert
        assertThat(result.getId()).isEqualTo(problem.getId());
        assertThat(result.getProblemType()).isEqualTo("ContinuousProblem");
        assertThat(result.getAgglomerativeClustering()).isNotNull();
        assertThat(result.getAgglomerativeClustering().getClusterSize()).isEqualTo(problem.getAgglomerativeClustering().getClusterSize());
        // Verify new color and shape fields
        assertThat(result.getColorStart()).isEqualTo(problem.getColorStart());
        assertThat(result.getColorEnd()).isEqualTo(problem.getColorEnd());
        assertThat(result.getStartShape()).isEqualTo(problem.getStartShape());
        assertThat(result.getEndShape()).isEqualTo(problem.getEndShape());
        assertThat(result.getDefaultShape()).isEqualTo(problem.getDefaultShape());
    }

    @Test
    void updateFromDTO_shouldUpdateProblemFields() {
        // Arrange
        Problem problem = ProblemMother.validDiscreteProblem();
        String originalName = problem.getName();
        Color newColor = Color.decode("#ec4899");
        Color newColorStart = Color.decode("#00ff00");
        Color newColorEnd = Color.decode("#ff0000");
        double newVertexSize = 2.0;
        double newArrowSize = 4.0;
        
        UpdateProblemDTO updateDTO = UpdateProblemDTO.builder()
                .name("Updated Problem Name")
                .color(newColor)
                .colorStart(newColorStart)
                .colorEnd(newColorEnd)
                .startShape(Shape.TRIANGLE)
                .endShape(Shape.ROUND)
                .defaultShape(Shape.STAR)
                .vertexSize(newVertexSize)
                .arrowSize(newArrowSize)
                .isMaximization(true)
                .build();

        // Act
        problemMapper.updateFromDTO(problem, updateDTO);

        // Assert
        assertThat(problem.getName()).isEqualTo("Updated Problem Name");
        assertThat(problem.getName()).isNotEqualTo(originalName);
        assertThat(problem.getColor()).isEqualTo(newColor);
        assertThat(problem.getColorStart()).isEqualTo(newColorStart);
        assertThat(problem.getColorEnd()).isEqualTo(newColorEnd);
        assertThat(problem.getStartShape()).isEqualTo(Shape.TRIANGLE);
        assertThat(problem.getEndShape()).isEqualTo(Shape.ROUND);
        assertThat(problem.getDefaultShape()).isEqualTo(Shape.STAR);
        assertThat(problem.getVertexSize()).isEqualTo(newVertexSize);
        assertThat(problem.getArrowSize()).isEqualTo(newArrowSize);
        assertThat(problem.isMaximization()).isTrue();
    }
}
