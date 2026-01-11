package com.tfg.backend.user.application;

import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.UnauthorizedOperationException;
import com.tfg.backend.user.domain.exceptions.UserNotFoundException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadAllUsersUseCaseTest {
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private ReadAllUsersUseCase readAllUsersUseCase;
    
    private User adminUser;
    private User regularUser;
    private User anotherUser;
    private ResponseUserDTO adminResponseDTO;
    private ResponseUserDTO userResponseDTO;
    private ResponseUserDTO anotherUserResponseDTO;
    
    @BeforeEach
    void setUp() {
        adminUser = UserMother.validAdminUser();
        regularUser = UserMother.validRegularUser();
        anotherUser = UserMother.validRegularUser();
        
        adminResponseDTO = ResponseUserDTO.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .email(adminUser.getEmail())
                .role(adminUser.getRole())
                .build();
        
        userResponseDTO = ResponseUserDTO.builder()
                .id(regularUser.getId())
                .username(regularUser.getUsername())
                .email(regularUser.getEmail())
                .role(regularUser.getRole())
                .build();
        
        anotherUserResponseDTO = ResponseUserDTO.builder()
                .id(anotherUser.getId())
                .username(anotherUser.getUsername())
                .email(anotherUser.getEmail())
                .role(anotherUser.getRole())
                .build();
    }
    
    @Test
    void execute_adminUser_shouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(adminUser, regularUser, anotherUser);
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.findAll()).thenReturn(users);
        when(userMapper.fromObjectToDTO(adminUser)).thenReturn(adminResponseDTO);
        when(userMapper.fromObjectToDTO(regularUser)).thenReturn(userResponseDTO);
        when(userMapper.fromObjectToDTO(anotherUser)).thenReturn(anotherUserResponseDTO);
        
        // Act
        List<ResponseUserDTO> result = readAllUsersUseCase.execute(adminUser.getId());
        
        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(adminResponseDTO, userResponseDTO, anotherUserResponseDTO);
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).findAll();
    }
    
    @Test
    void execute_adminNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentAdminId = UUID.randomUUID();
        when(userRepositoryJPA.findById(nonExistentAdminId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> readAllUsersUseCase.execute(nonExistentAdminId)
        );
        
        verify(userRepositoryJPA).findById(nonExistentAdminId);
        verify(userRepositoryJPA, never()).findAll();
    }
    
    @Test
    void execute_nonAdminUser_shouldThrowUnauthorizedOperationException() {
        // Arrange
        when(userRepositoryJPA.findById(regularUser.getId())).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        assertThrows(
                UnauthorizedOperationException.class,
                () -> readAllUsersUseCase.execute(regularUser.getId())
        );
        
        verify(userRepositoryJPA).findById(regularUser.getId());
        verify(userRepositoryJPA, never()).findAll();
    }
}
