package com.tfg.backend.problem.application.dto;

import com.tfg.backend.problem.domain.DistanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardPartitioningDTO {
    private UUID id;
    private int hypercube;
    private int minBound;
    private int maxBound;
    private int numberDimension;
}
