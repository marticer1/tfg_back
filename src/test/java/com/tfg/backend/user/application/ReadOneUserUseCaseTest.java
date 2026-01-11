package com.tfg.backend.user.application;

import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.mapper.UserMapper;
import com.tfg.backend.user.domain.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadOneUserUseCaseTest {
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private ReadOneUserUseCase readOneUserUseCase;
    
    private User user;
    private ResponseUserDTO responseDTO;
    
    @BeforeEach
    void setUp() {
        user = UserMother.validRegularUser();
        responseDTO = ResponseUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Test
    void execute_existingUser_shouldReturnResponseDTO() {
        // Arrange
        when(userRepositoryJPA.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.fromObjectToDTO(user)).thenReturn(responseDTO);
        
        // Act
        ResponseUserDTO result = readOneUserUseCase.execute(user.getId());
        
        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(userRepositoryJPA).findById(user.getId());
        verify(userMapper).fromObjectToDTO(user);
    }
    
    @Test
    void execute_nonExistingUser_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepositoryJPA.findById(nonExistentUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> readOneUserUseCase.execute(nonExistentUserId)
        );
        
        verify(userRepositoryJPA).findById(nonExistentUserId);
        verify(userMapper, never()).fromObjectToDTO(any());
    }
    
    @Test
    void executeByUsername_existingUser_shouldReturnResponseDTO() {
        // Arrange
        when(userRepositoryJPA.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userMapper.fromObjectToDTO(user)).thenReturn(responseDTO);
        
        // Act
        ResponseUserDTO result = readOneUserUseCase.executeByUsername(user.getUsername());
        
        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(userRepositoryJPA).findByUsername(user.getUsername());
        verify(userMapper).fromObjectToDTO(user);
    }
    
    @Test
    void executeByUsername_nonExistingUser_shouldThrowUserNotFoundException() {
        // Arrange
        String nonExistentUsername = "nonexistent";
        when(userRepositoryJPA.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> readOneUserUseCase.executeByUsername(nonExistentUsername)
        );
        
        verify(userRepositoryJPA).findByUsername(nonExistentUsername);
        verify(userMapper, never()).fromObjectToDTO(any());
    }
}
