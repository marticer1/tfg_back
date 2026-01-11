package com.tfg.backend.problemCollection.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.awt.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemCollection {
    @Id
    private UUID id;
    @NotBlank(message = "Name can not be blank")
    private String name;
    @NotNull(message = "Color can not be null")
    private Color color;
}
