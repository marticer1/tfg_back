package com.tfg.backend.algorithm.application.dto;

import com.tfg.backend.algorithm.domain.NodeType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationNodeDTO {
    @NotNull(message = "Node type cannot be null")
    private NodeType type;
    
    @NotNull(message = "Position cannot be null")
    private Position3DDTO position;
}
