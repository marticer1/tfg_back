package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.application.DeleteUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserDeleteController {
    
    private final DeleteUserUseCase deleteUserUseCase;
    
    public UserDeleteController(DeleteUserUseCase deleteUserUseCase) {
        this.deleteUserUseCase = deleteUserUseCase;
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Admin-Id") UUID adminId) {
        deleteUserUseCase.execute(userId, adminId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
