package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.domain.exceptions.ProblemCollectionNotFoundException;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
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
class UpdateProblemCollectionUseCaseTest {

    @Mock
    private ProblemCollectionMapper problemCollectionMapper;

    @Mock
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @InjectMocks
    private UpdateProblemCollectionUseCase updateProblemCollectionUseCase;

    private UpdateProblemCollectionDTO updateDTO;
    private ProblemCollection problemCollection;
    private ResponseProblemCollectionDTO responseDTO;
    private UUID collectionId;

    @BeforeEach
    void setUp() {
        // Arrange
        updateDTO = mock(UpdateProblemCollectionDTO.class);
        problemCollection = mock(ProblemCollection.class);
        responseDTO = mock(ResponseProblemCollectionDTO.class);
        collectionId = UUID.randomUUID();
    }

    @Test
    void execute_validIdAndDTO_shouldUpdateAndReturnResponse() {
        // Arrange
        when(problemCollectionRepositoryJPA.findById(collectionId)).thenReturn(Optional.of(problemCollection));
        when(problemCollectionRepositoryJPA.save(problemCollection)).thenReturn(problemCollection);
        when(problemCollectionMapper.fromObjectToDTO(problemCollection)).thenReturn(responseDTO);

        // Act
        ResponseProblemCollectionDTO result = updateProblemCollectionUseCase.execute(collectionId, updateDTO);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(problemCollectionRepositoryJPA).findById(collectionId);
        verify(problemCollectionMapper).updateFromDTO(problemCollection, updateDTO);
        verify(problemCollectionRepositoryJPA).save(problemCollection);
        verify(problemCollectionMapper).fromObjectToDTO(problemCollection);
    }

    @Test
    void execute_whenCollectionIdDoesNotExist_shouldThrowProblemCollectionNotFoundException() {
        // Arrange
        when(problemCollectionRepositoryJPA.findById(collectionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ProblemCollectionNotFoundException.class,
                () -> updateProblemCollectionUseCase.execute(collectionId, updateDTO)
        );

        verify(problemCollectionRepositoryJPA).findById(collectionId);
        verify(problemCollectionMapper, never()).updateFromDTO(any(), any());
        verify(problemCollectionRepositoryJPA, never()).save(any());
        verify(problemCollectionMapper, never()).fromObjectToDTO(any());
    }
}
