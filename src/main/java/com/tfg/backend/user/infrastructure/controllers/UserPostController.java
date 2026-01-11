package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.application.CreateUserUseCase;
import com.tfg.backend.user.application.dto.RegistrationUserDTO;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserPostController {
    
    private final CreateUserUseCase createUserUseCase;
    
    public UserPostController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }
    
    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(
            @RequestHeader("X-Admin-Id") UUID adminId,
            @Valid @RequestBody RegistrationUserDTO registrationUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createUserUseCase.execute(registrationUserDTO, adminId));
    }
    
    @PostMapping("/initial-admin")
    public ResponseEntity<ResponseUserDTO> createInitialAdmin(
            @Valid @RequestBody RegistrationUserDTO registrationUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createUserUseCase.executeInitialAdmin(registrationUserDTO));
    }
}
