package com.tfg.backend.problem.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tfg.backend.common.ColorDeserializer;
import com.tfg.backend.problem.domain.Shape;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProblemDTO {
    @NotBlank(message = "Name can not be blank")
    private String name;

    @NotNull(message = "Color can not be null")
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color color;
    
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color colorStart;
    
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color colorEnd;
    
    private Shape startShape;
    
    private Shape endShape;
    
    private Shape defaultShape;

    @NotNull(message = "Vertex size cannot be null")
    private Double vertexSize;

    @NotNull(message = "Arrow size cannot be null")
    private Double arrowSize;
    
    @NotNull(message = "Is maximization cannot be null")
    private Boolean isMaximization;
}
