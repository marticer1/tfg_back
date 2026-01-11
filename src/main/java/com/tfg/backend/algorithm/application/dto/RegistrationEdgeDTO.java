package com.tfg.backend.algorithm.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationEdgeDTO {
    private UUID sourceNodeId;
    private UUID targetNodeId;
}
