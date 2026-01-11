package com.tfg.backend.user.application;

import com.tfg.backend.security.JwtUtil;
import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.LoginRequestDTO;
import com.tfg.backend.user.application.dto.LoginResponseDTO;
import com.tfg.backend.user.domain.User;
import com.tfg.backend.user.domain.exceptions.InvalidCredentialsException;
import com.tfg.backend.user.infrastructure.repositories.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private LoginUseCase loginUseCase;
    
    private User existingUser;
    private LoginRequestDTO validLoginRequest;
    
    @BeforeEach
    void setUp() {
        existingUser = UserMother.validRegularUser();
        validLoginRequest = LoginRequestDTO.builder()
                .email(existingUser.getEmail())
                .password(existingUser.getPassword())
                .build();
    }
    
    @Test
    void execute_validCredentials_shouldReturnLoginResponse() {
        // Arrange
        when(userRepositoryJPA.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(any(), anyString())).thenReturn("mock-jwt-token");
        
        // Act
        LoginResponseDTO result = loginUseCase.execute(validLoginRequest);
        
        // Assert
        assertThat(result.getId()).isEqualTo(existingUser.getId());
        assertThat(result.getUsername()).isEqualTo(existingUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(existingUser.getEmail());
        assertThat(result.getRole()).isEqualTo(existingUser.getRole());
        assertThat(result.getMessage()).isEqualTo("Login successful");
        assertThat(result.getToken()).isEqualTo("mock-jwt-token");
        verify(userRepositoryJPA).findByEmail(validLoginRequest.getEmail());
    }
    
    @Test
    void execute_userNotFound_shouldThrowInvalidCredentialsException() {
        // Arrange
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();
        when(userRepositoryJPA.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(loginRequest)
        );
        
        verify(userRepositoryJPA).findByEmail(loginRequest.getEmail());
    }
    
    @Test
    void execute_wrongPassword_shouldThrowInvalidCredentialsException() {
        // Arrange
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(existingUser.getEmail())
                .password("wrongpassword")
                .build();
        when(userRepositoryJPA.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(existingUser));
        
        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.execute(loginRequest)
        );
        
        verify(userRepositoryJPA).findByEmail(loginRequest.getEmail());
    }
    
    @Test
    void execute_adminUser_shouldReturnLoginResponseWithAdminRole() {
        // Arrange
        User adminUser = UserMother.validAdminUser();
        LoginRequestDTO adminLoginRequest = LoginRequestDTO.builder()
                .email(adminUser.getEmail())
                .password(adminUser.getPassword())
                .build();
        when(userRepositoryJPA.findByEmail(adminLoginRequest.getEmail())).thenReturn(Optional.of(adminUser));
        when(jwtUtil.generateToken(any(), anyString())).thenReturn("mock-admin-jwt-token");
        
        // Act
        LoginResponseDTO result = loginUseCase.execute(adminLoginRequest);
        
        // Assert
        assertThat(result.getId()).isEqualTo(adminUser.getId());
        assertThat(result.getRole()).isEqualTo(adminUser.getRole());
        assertThat(result.getMessage()).isEqualTo("Login successful");
        assertThat(result.getToken()).isEqualTo("mock-admin-jwt-token");
    }
}
