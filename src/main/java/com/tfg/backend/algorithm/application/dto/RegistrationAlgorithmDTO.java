package com.tfg.backend.algorithm.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tfg.backend.common.ColorDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Color;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationAlgorithmDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    @NotNull(message = "Color cannot be null")
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color color;
    
    private FileDTO file;
    
    private List<RegistrationNodeDTO> nodes;
}
