package com.tfg.backend.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    private UUID id;
    
    @NotBlank(message = "Username cannot be blank")
    @Column(unique = true)
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
}
