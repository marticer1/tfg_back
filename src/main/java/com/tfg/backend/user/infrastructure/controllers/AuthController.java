package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.application.LoginUseCase;
import com.tfg.backend.user.application.dto.LoginRequestDTO;
import com.tfg.backend.user.application.dto.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    
    private final LoginUseCase loginUseCase;
    
    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(loginUseCase.execute(loginRequest));
    }
}
