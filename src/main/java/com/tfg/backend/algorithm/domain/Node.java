package com.tfg.backend.algorithm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Node {
    @Id
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Node type cannot be null")
    private NodeType type;
    
    @Embedded
    @NotNull(message = "Position cannot be null")
    private Position3D position;
    
    @ManyToOne
    @JoinColumn(name = "algorithm_id")
    @NotNull(message = "Algorithm cannot be null")
    private Algorithm algorithm;
    
    @OneToMany(mappedBy = "sourceNode", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Edge> outgoingEdges = new ArrayList<>();
    
    @OneToMany(mappedBy = "targetNode", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Edge> incomingEdges = new ArrayList<>();
}
