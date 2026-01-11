package com.tfg.backend.algorithm.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResponseEdgeDTO {
    private UUID id;
    private UUID sourceNodeId;
    private UUID targetNodeId;
    private UUID algorithmId;
}
