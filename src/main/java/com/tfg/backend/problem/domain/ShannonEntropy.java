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
public class ShannonEntropy {
    @Id
    private UUID id;
    
    private double partitioning;
}
