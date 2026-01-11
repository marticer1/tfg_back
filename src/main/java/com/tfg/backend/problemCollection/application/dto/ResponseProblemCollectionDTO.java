package com.tfg.backend.problemCollection.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tfg.backend.common.ColorSerializer;
import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.util.UUID;

@Data
@Builder
public class ResponseProblemCollectionDTO {
    private UUID id;
    private String name;
    @JsonSerialize(using = ColorSerializer.class)
    private Color color;
}
