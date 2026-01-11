package com.tfg.backend.user.infrastructure.controllers;

import com.tfg.backend.user.application.UpdateUserUseCase;
import com.tfg.backend.user.application.dto.ResponseUserDTO;
import com.tfg.backend.user.application.dto.UpdateUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserPutController {
    
    private final UpdateUserUseCase updateUserUseCase;
    
    public UserPutController(UpdateUserUseCase updateUserUseCase) {
        this.updateUserUseCase = updateUserUseCase;
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDTO> updateUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Admin-Id") UUID adminId,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(updateUserUseCase.execute(userId, updateUserDTO, adminId));
    }
}
