package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadAllProblemCollectionsUseCaseTest {

    @Mock
    private ProblemCollectionMapper problemCollectionMapper;

    @Mock
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @InjectMocks
    private ReadAllProblemCollectionsUseCase readAllProblemCollectionsUseCase;

    private ProblemCollection problemCollection1;
    private ProblemCollection problemCollection2;
    private ResponseProblemCollectionDTO responseDTO1;
    private ResponseProblemCollectionDTO responseDTO2;

    @BeforeEach
    void setUp() {
        problemCollection1 = mock(ProblemCollection.class);
        problemCollection2 = mock(ProblemCollection.class);
        responseDTO1 = mock(ResponseProblemCollectionDTO.class);
        responseDTO2 = mock(ResponseProblemCollectionDTO.class);
    }

    @Test
    void execute_shouldReturnAllProblemCollections() {
        // Arrange
        List<ProblemCollection> problemCollections = Arrays.asList(problemCollection1, problemCollection2);
        when(problemCollectionRepositoryJPA.findAll()).thenReturn(problemCollections);
        when(problemCollectionMapper.fromObjectToDTO(problemCollection1)).thenReturn(responseDTO1);
        when(problemCollectionMapper.fromObjectToDTO(problemCollection2)).thenReturn(responseDTO2);

        // Act
        List<ResponseProblemCollectionDTO> result = readAllProblemCollectionsUseCase.execute();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(responseDTO1, responseDTO2);
        verify(problemCollectionRepositoryJPA).findAll();
        verify(problemCollectionMapper).fromObjectToDTO(problemCollection1);
        verify(problemCollectionMapper).fromObjectToDTO(problemCollection2);
    }

    @Test
    void execute_emptyRepository_shouldReturnEmptyList() {
        // Arrange
        when(problemCollectionRepositoryJPA.findAll()).thenReturn(List.of());

        // Act
        List<ResponseProblemCollectionDTO> result = readAllProblemCollectionsUseCase.execute();

        // Assert
        assertThat(result).isEmpty();
        verify(problemCollectionRepositoryJPA).findAll();
        verify(problemCollectionMapper, never()).fromObjectToDTO(any());
    }
}
