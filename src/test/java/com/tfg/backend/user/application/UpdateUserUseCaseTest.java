package com.tfg.backend.user.application;

import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.dto.UpdateUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.Role;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserAlreadyExistsException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;
    
    private User adminUser;
    private User regularUser;
    private User userToUpdate;
    private UpdateUserDTO updateUserDTO;
    private ResponseUserDTO responseDTO;
    
    @BeforeEach
    void setUp() {
        adminUser = UserMother.validAdminUser();
        regularUser = UserMother.validRegularUser();
        userToUpdate = UserMother.validRegularUser();
        updateUserDTO = UserMother.validUpdateUserDTO();
        
        responseDTO = ResponseUserDTO.builder()
                .id(userToUpdate.getId())
                .username(updateUserDTO.getUsername())
                .email(updateUserDTO.getEmail())
                .role(updateUserDTO.getRole())
                .build();
    }
    
    @Test
    void execute_adminUpdatesExistingUser_shouldUpdateAndReturnResponse() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepositoryJPA.existsByUsername(updateUserDTO.getUsername())).thenReturn(false);
        when(userRepositoryJPA.existsByEmail(updateUserDTO.getEmail())).thenReturn(false);
        when(userRepositoryJPA.save(any(User.class))).thenReturn(userToUpdate);
        when(userMapper.fromObjectToDTO(any(User.class))).thenReturn(responseDTO);
        
        // Act
        ResponseUserDTO result = updateUserUseCase.execute(userToUpdate.getId(), updateUserDTO, adminUser.getId());
        
        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).findById(userToUpdate.getId());
        verify(userRepositoryJPA).save(any(User.class));
        verify(userMapper).fromObjectToDTO(any(User.class));
    }
    
    @Test
    void execute_adminNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentAdminId = UUID.randomUUID();
        when(userRepositoryJPA.findById(nonExistentAdminId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> updateUserUseCase.execute(userToUpdate.getId(), updateUserDTO, nonExistentAdminId)
        );
        
        verify(userRepositoryJPA).findById(nonExistentAdminId);
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_nonAdminUser_shouldThrowUnauthorizedOperationException() {
        // Arrange
        when(userRepositoryJPA.findById(regularUser.getId())).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        assertThrows(
                UnauthorizedOperationException.class,
                () -> updateUserUseCase.execute(userToUpdate.getId(), updateUserDTO, regularUser.getId())
        );
        
        verify(userRepositoryJPA).findById(regularUser.getId());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_userToUpdateNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findById(nonExistentUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> updateUserUseCase.execute(nonExistentUserId, updateUserDTO, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).findById(nonExistentUserId);
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_duplicateUsername_shouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepositoryJPA.existsByUsername(updateUserDTO.getUsername())).thenReturn(true);
        
        // Act & Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> updateUserUseCase.execute(userToUpdate.getId(), updateUserDTO, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).findById(userToUpdate.getId());
        verify(userRepositoryJPA).existsByUsername(updateUserDTO.getUsername());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_duplicateEmail_shouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepositoryJPA.existsByUsername(updateUserDTO.getUsername())).thenReturn(false);
        when(userRepositoryJPA.existsByEmail(updateUserDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> updateUserUseCase.execute(userToUpdate.getId(), updateUserDTO, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).findById(userToUpdate.getId());
        verify(userRepositoryJPA).existsByUsername(updateUserDTO.getUsername());
        verify(userRepositoryJPA).existsByEmail(updateUserDTO.getEmail());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_partialUpdate_shouldUpdateOnlyProvidedFields() {
        // Arrange
        UpdateUserDTO partialUpdateDTO = UpdateUserDTO.builder()
                .username("newusername")
                .build();
        
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepositoryJPA.existsByUsername("newusername")).thenReturn(false);
        when(userRepositoryJPA.save(any(User.class))).thenReturn(userToUpdate);
        when(userMapper.fromObjectToDTO(any(User.class))).thenReturn(responseDTO);
        
        // Act
        ResponseUserDTO result = updateUserUseCase.execute(userToUpdate.getId(), partialUpdateDTO, adminUser.getId());
        
        // Assert
        assertThat(result).isNotNull();
        verify(userRepositoryJPA).save(any(User.class));
    }
}
