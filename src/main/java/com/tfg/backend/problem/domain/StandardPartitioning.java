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
public class StandardPartitioning {
    @Id
    private UUID id;
    
    private int hypercube;
    private int minBound;
    private int maxBound;
    private int numberDimension;
}
