package com.tfg.backend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgglomerativeClustering {
    @Id
    private UUID id;
    
    private double clusterSize;
    private double volumeSize;
    
    @Enumerated(EnumType.STRING)
    private DistanceType distance;
}
