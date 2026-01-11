package com.tfg.backend.algorithm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Edge {
    @Id
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "source_node_id")
    @NotNull(message = "Source node cannot be null")
    private Node sourceNode;
    
    @ManyToOne
    @JoinColumn(name = "target_node_id")
    @NotNull(message = "Target node cannot be null")
    private Node targetNode;
    
    @ManyToOne
    @JoinColumn(name = "algorithm_id")
    @NotNull(message = "Algorithm cannot be null")
    private Algorithm algorithm;
}
