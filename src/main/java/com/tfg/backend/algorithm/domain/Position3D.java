package com.tfg.backend.algorithm.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position3D {
    @NotNull(message = "X coordinate cannot be null")
    private Double x;
    
    @NotNull(message = "Y coordinate cannot be null")
    private Double y;
    
    @NotNull(message = "Z coordinate cannot be null")
    private Double z;
}
