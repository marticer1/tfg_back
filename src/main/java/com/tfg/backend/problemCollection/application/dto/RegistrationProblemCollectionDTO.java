package com.tfg.backend.problemCollection.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tfg.backend.common.ColorDeserializer;
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
public class RegistrationProblemCollectionDTO {
    @NotBlank (message = "Name can not be blank")
    private String name;
    @NotNull (message = "Color can not be null")
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color color;
}
