package com.tfg.backend.problemCollection.application.mapper;

import com.tfg.backend.problemCollection.ProblemCollectionMother;
import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProblemCollectionMapperTest {

    @InjectMocks
    private ProblemCollectionMapper problemCollectionMapper;

    @Test
    void fromDTOtoObject() {
        //Arrange
        RegistrationProblemCollectionDTO registrationProblemCollectionDTO = ProblemCollectionMother.validRegistrationProblemCollectionDTO();

        //Act
        ProblemCollection result = problemCollectionMapper.fromDTOtoObject(registrationProblemCollectionDTO);

        //Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Collection");
        assertThat(result.getColor()).isEqualTo(Color.BLACK);
    }

    @Test
    void fromObjectToDTO() {
        // Arrange
        ProblemCollection problemCollection = ProblemCollectionMother.validProblemCollection();

        // Act
        ResponseProblemCollectionDTO result = problemCollectionMapper.fromObjectToDTO(problemCollection);

        // Assert
        assertThat(result.getId()).isEqualTo(problemCollection.getId());
        assertThat(result.getName()).isEqualTo(problemCollection.getName());
        assertThat(result.getColor()).isEqualTo(problemCollection.getColor());
    }

    @Test
    void updateFromDTO() {
        // Arrange
        UUID originalId = UUID.randomUUID();
        ProblemCollection problemCollection = ProblemCollection.builder()
                .id(originalId)
                .name("Original Name")
                .color(Color.RED)
                .build();
        UpdateProblemCollectionDTO updateDTO = ProblemCollectionMother.validUpdateProblemCollectionDTO();

        // Act
        problemCollectionMapper.updateFromDTO(problemCollection, updateDTO);

        // Assert
        assertThat(problemCollection.getId()).isEqualTo(originalId); // ID should not change
        assertThat(problemCollection.getName()).isEqualTo("Updated Collection");
        assertThat(problemCollection.getColor()).isEqualTo(Color.BLUE);
    }
}