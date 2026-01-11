package com.tfg.backend.user.application;

import com.tfg.backend.user.UserMother;
import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
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
class CreateUserUseCaseTest {
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private UserRepositoryJPA userRepositoryJPA;
    
    @InjectMocks
    private CreateUserUseCase createUserUseCase;
    
    private User adminUser;
    private User regularUser;
    private User newUser;
    private RegistrationUserDTO registrationDTO;
    private ResponseUserDTO responseDTO;
    
    @BeforeEach
    void setUp() {
        adminUser = UserMother.validAdminUser();
        regularUser = UserMother.validRegularUser();
        registrationDTO = UserMother.validRegistrationUserDTO();
        
        newUser = User.builder()
                .id(UUID.randomUUID())
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .password(registrationDTO.getPassword())
                .role(Role.USER)
                .build();
        
        responseDTO = ResponseUserDTO.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .role(newUser.getRole())
                .build();
    }
    
    @Test
    void execute_validDTOAndAdmin_shouldCreateAndReturnResponse() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.existsByUsername(registrationDTO.getUsername())).thenReturn(false);
        when(userRepositoryJPA.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
        when(userMapper.fromDTOtoObject(registrationDTO)).thenReturn(newUser);
        when(userRepositoryJPA.save(newUser)).thenReturn(newUser);
        when(userMapper.fromObjectToDTO(newUser)).thenReturn(responseDTO);
        
        // Act
        ResponseUserDTO result = createUserUseCase.execute(registrationDTO, adminUser.getId());
        
        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).existsByUsername(registrationDTO.getUsername());
        verify(userRepositoryJPA).existsByEmail(registrationDTO.getEmail());
        verify(userMapper).fromDTOtoObject(registrationDTO);
        verify(userRepositoryJPA).save(newUser);
        verify(userMapper).fromObjectToDTO(newUser);
    }
    
    @Test
    void execute_adminNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        UUID nonExistentAdminId = UUID.randomUUID();
        when(userRepositoryJPA.findById(nonExistentAdminId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> createUserUseCase.execute(registrationDTO, nonExistentAdminId)
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
                () -> createUserUseCase.execute(registrationDTO, regularUser.getId())
        );
        
        verify(userRepositoryJPA).findById(regularUser.getId());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_duplicateUsername_shouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.existsByUsername(registrationDTO.getUsername())).thenReturn(true);
        
        // Act & Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> createUserUseCase.execute(registrationDTO, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).existsByUsername(registrationDTO.getUsername());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void execute_duplicateEmail_shouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepositoryJPA.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepositoryJPA.existsByUsername(registrationDTO.getUsername())).thenReturn(false);
        when(userRepositoryJPA.existsByEmail(registrationDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> createUserUseCase.execute(registrationDTO, adminUser.getId())
        );
        
        verify(userRepositoryJPA).findById(adminUser.getId());
        verify(userRepositoryJPA).existsByUsername(registrationDTO.getUsername());
        verify(userRepositoryJPA).existsByEmail(registrationDTO.getEmail());
        verify(userRepositoryJPA, never()).save(any());
    }
    
    @Test
    void executeInitialAdmin_validDTO_shouldCreateAndReturnAdminResponse() {
        // Arrange
        RegistrationUserDTO adminRegistrationDTO = UserMother.validRegistrationAdminDTO();
        User adminCreated = User.builder()
                .id(UUID.randomUUID())
                .username(adminRegistrationDTO.getUsername())
                .email(adminRegistrationDTO.getEmail())
                .password(adminRegistrationDTO.getPassword())
                .role(Role.ADMIN)
                .build();
        
        ResponseUserDTO adminResponseDTO = ResponseUserDTO.builder()
                .id(adminCreated.getId())
                .username(adminCreated.getUsername())
                .email(adminCreated.getEmail())
                .role(Role.ADMIN)
                .build();
        
        when(userRepositoryJPA.existsByUsername(adminRegistrationDTO.getUsername())).thenReturn(false);
        when(userRepositoryJPA.existsByEmail(adminRegistrationDTO.getEmail())).thenReturn(false);
        when(userMapper.fromDTOtoObject(adminRegistrationDTO)).thenReturn(adminCreated);
        when(userRepositoryJPA.save(adminCreated)).thenReturn(adminCreated);
        when(userMapper.fromObjectToDTO(adminCreated)).thenReturn(adminResponseDTO);
        
        // Act
        ResponseUserDTO result = createUserUseCase.executeInitialAdmin(adminRegistrationDTO);
        
        // Assert
        assertThat(result).isEqualTo(adminResponseDTO);
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepositoryJPA).existsByUsername(adminRegistrationDTO.getUsername());
        verify(userRepositoryJPA).existsByEmail(adminRegistrationDTO.getEmail());
        verify(userMapper).fromDTOtoObject(adminRegistrationDTO);
        verify(userRepositoryJPA).save(adminCreated);
        verify(userMapper).fromObjectToDTO(adminCreated);
    }
}
