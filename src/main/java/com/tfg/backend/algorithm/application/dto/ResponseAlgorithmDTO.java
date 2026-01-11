package com.tfg.backend.algorithm.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tfg.backend.common.ColorSerializer;
import lombok.Builder;
import lombok.Data;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ResponseAlgorithmDTO {
    private UUID id;
    private String name;
    @JsonSerialize(using = ColorSerializer.class)
    private Color color;
    private FileDTO file;
    private UUID problemId;
    private List<ResponseNodeDTO> nodes;
    private List<ResponseEdgeDTO> edges;
    private Integer nodeCount;
    private Integer edgeCount;
    private Integer componentCount;
}
