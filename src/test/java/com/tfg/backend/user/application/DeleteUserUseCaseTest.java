package com.tfg.backend.user.application;

import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.CannotDeleteSelfException;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;
    
    private User adminUser;
    private User regularUser;
    private User userToDelete;
    
    @BeforeEach
    void setUp() {
        adminUser = UserMother.validAdminUser();
        regularUser = UserMother.validRegularUser();
        userToDelete = UserMother.validRegularUser();
    }
    
    @Test
    void execute_adminDeletesExistingUser_shouldDeleteUser() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.existsById(userToDelete.getId())).thenReturn(true);
        
        // Act & Assert
        assertDoesNotThrow(() -> deleteUserUseCase.execute(userToDelete.getId(), adminUser.getId()));
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).existsById(userToDelete.getId());
        verify(userRepositoryJPA).deleteById(userToDelete.getId());
    }
    
    @Test
    void execute_adminNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentAdminId = UUID.randomUUID();
        when(userRepositoryJPA.findById(nonExistentAdminId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> deleteUserUseCase.execute(userToDelete.getId(), nonExistentAdminId)
        );
        
        verify(userRepositoryJPA).findById(nonExistentAdminId);
        verify(userRepositoryJPA, never()).deleteById(any());
    }
    
    @Test
    void execute_nonAdminUser_shouldThrowUnauthorizedOperationException() {
        // Arrange
        when(userRepositoryJPA.findById(regularUser.getId())).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        assertThrows(
                UnauthorizedOperationException.class,
                () -> deleteUserUseCase.execute(userToDelete.getId(), regularUser.getId())
        );
        
        verify(userRepositoryJPA).findById(regularUser.getId());
        verify(userRepositoryJPA, never()).deleteById(any());
    }
    
    @Test
    void execute_adminDeletesSelf_shouldThrowCannotDeleteSelfException() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        
        // Act & Assert
        assertThrows(
                CannotDeleteSelfException.class,
                () -> deleteUserUseCase.execute(adminUser.getId(), adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA, never()).deleteById(any());
    }
    
    @Test
    void execute_userToDeleteNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.existsById(nonExistentUserId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> deleteUserUseCase.execute(nonExistentUserId, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).existsById(nonExistentUserId);
        verify(userRepositoryJPA, never()).deleteById(any());
    }
}
