package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.domain.exceptions.ProblemCollectionAlreadyExistException;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
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
class CreateProblemCollectionUseCaseTest {

    @Mock
    private ProblemCollectionMapper problemCollectionMapper;

    @Mock
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @InjectMocks
    private CreateProblemCollectionUseCase createProblemCollectionUseCase;

    private RegistrationProblemCollectionDTO registrationDTO;
    private ProblemCollection problemCollection;
    private ResponseProblemCollectionDTO responseDTO;
    private UUID collectionId;

    @BeforeEach
    void setUp() {
        // Arrange
        registrationDTO = mock(RegistrationProblemCollectionDTO.class);
        problemCollection = mock(ProblemCollection.class);
        responseDTO = mock(ResponseProblemCollectionDTO.class);
        collectionId = UUID.randomUUID();

        when(problemCollection.getId()).thenReturn(collectionId);
    }

    @Test
    void execute_validDTO_shouldCreateAndReturnResponse() {
        // Arrange
        // Arrange - mapping and repository behavior
        when(problemCollectionMapper.fromDTOtoObject(registrationDTO)).thenReturn(problemCollection);
        when(problemCollectionRepositoryJPA.existsById(collectionId)).thenReturn(false);
        when(problemCollectionRepositoryJPA.save(problemCollection)).thenReturn(problemCollection);
        when(problemCollectionMapper.fromObjectToDTO(problemCollection)).thenReturn(responseDTO);

        // Act
        ResponseProblemCollectionDTO result = createProblemCollectionUseCase.execute(registrationDTO);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(problemCollectionMapper).fromDTOtoObject(registrationDTO);
        verify(problemCollectionRepositoryJPA).existsById(collectionId);
        verify(problemCollectionRepositoryJPA).save(problemCollection);
        verify(problemCollectionMapper).fromObjectToDTO(problemCollection);
    }

    @Test
    void execute_whenCollectionIdAlreadyExists_shouldThrowProblemCollectionAlreadyExistException() {
        // Arrange
        when(problemCollectionMapper.fromDTOtoObject(registrationDTO)).thenReturn(problemCollection);
        when(problemCollectionRepositoryJPA.existsById(collectionId)).thenReturn(true);

        // Act & Assert
        assertThrows(
                ProblemCollectionAlreadyExistException.class,
                () -> createProblemCollectionUseCase.execute(registrationDTO)
        );

        verify(problemCollectionMapper).fromDTOtoObject(registrationDTO);
        verify(problemCollectionRepositoryJPA).existsById(collectionId);
        verify(problemCollectionRepositoryJPA, never()).save(any());
        verify(problemCollectionMapper, never()).fromObjectToDTO(any());
    }
}