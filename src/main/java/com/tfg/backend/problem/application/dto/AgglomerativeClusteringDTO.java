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
public class AgglomerativeClusteringDTO {
    private UUID id;
    private double clusterSize;
    private double volumeSize;
    private DistanceType distance;
}
