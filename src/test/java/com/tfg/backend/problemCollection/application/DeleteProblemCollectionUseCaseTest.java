package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DeleteProblemCollectionUseCaseTest {

    @Mock
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @InjectMocks
    private DeleteProblemCollectionUseCase deleteProblemCollectionUseCase;

    private UUID collectionId;

    @BeforeEach
    void setUp() {
        collectionId = UUID.randomUUID();
    }

    @Test
    void execute_validId_shouldDeleteCollection() {
        // Arrange
        when(problemCollectionRepositoryJPA.existsById(collectionId)).thenReturn(true);

        // Act
        deleteProblemCollectionUseCase.execute(collectionId);

        // Assert
        verify(problemCollectionRepositoryJPA).existsById(collectionId);
        verify(problemCollectionRepositoryJPA).deleteById(collectionId);
    }

    @Test
    void execute_whenCollectionIdDoesNotExist_shouldReturnWithoutDeleting() {
        // Arrange
        when(problemCollectionRepositoryJPA.existsById(collectionId)).thenReturn(false);

        // Act
        deleteProblemCollectionUseCase.execute(collectionId);

        // Assert
        verify(problemCollectionRepositoryJPA).existsById(collectionId);
        verify(problemCollectionRepositoryJPA, never()).deleteById(any());
    }
}
