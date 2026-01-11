package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.application.ReadAllUsersUseCase;
import com.tfg.backend.user.application.ReadOneUserUseCase;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserGetController {
    
    private final ReadOneUserUseCase readOneUserUseCase;
    private final ReadAllUsersUseCase readAllUsersUseCase;
    
    public UserGetController(ReadOneUserUseCase readOneUserUseCase, 
                             ReadAllUsersUseCase readAllUsersUseCase) {
        this.readOneUserUseCase = readOneUserUseCase;
        this.readAllUsersUseCase = readAllUsersUseCase;
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(readOneUserUseCase.execute(userId));
    }
    
    @GetMapping
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers(
            @RequestHeader("X-Admin-Id") UUID adminId) {
        return ResponseEntity.ok(readAllUsersUseCase.execute(adminId));
    }
}
