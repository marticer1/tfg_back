package com.tfg.backend.algorithm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {
    @Id
    private UUID id;
    
    @NotBlank(message = "File name cannot be blank")
    private String fileName;
    
    private String fileType;
    
    @NotBlank(message = "File content cannot be blank")
    private String content;
}
