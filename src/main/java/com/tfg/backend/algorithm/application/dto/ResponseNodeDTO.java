package com.tfg.backend.algorithm.application.dto;

import com.tfg.backend.algorithm.domain.NodeType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResponseNodeDTO {
    private UUID id;
    private NodeType type;
    private Position3DDTO position;
    private UUID algorithmId;
}
