package com.tfg.backend.user.application.dto;

import com.tfg.backend.user.domain.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    
    private UUID id;
    private String username;
    private String email;
    private Role role;
    private String message;
    private String token;
}
