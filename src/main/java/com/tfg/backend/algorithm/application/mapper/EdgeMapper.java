package com.tfg.backend.algorithm.application.mapper;

import com.tfg.backend.algorithm.application.dto.RegistrationEdgeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseEdgeDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.Edge;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EdgeMapper {

    private final NodeRepositoryJPA nodeRepositoryJPA;
    private final AlgorithmRepositoryJPA algorithmRepositoryJPA;

    public EdgeMapper(NodeRepositoryJPA nodeRepositoryJPA, AlgorithmRepositoryJPA algorithmRepositoryJPA) {
        this.nodeRepositoryJPA = nodeRepositoryJPA;
        this.algorithmRepositoryJPA = algorithmRepositoryJPA;
    }

    public Edge fromDTOtoObject(RegistrationEdgeDTO dto, UUID algorithmId) {
        Node sourceNode = nodeRepositoryJPA.findById(dto.getSourceNodeId())
                .orElseThrow(() -> new RuntimeException("Source node not found with id: " + dto.getSourceNodeId()));
        
        Node targetNode = nodeRepositoryJPA.findById(dto.getTargetNodeId())
                .orElseThrow(() -> new RuntimeException("Target node not found with id: " + dto.getTargetNodeId()));
        
        Algorithm algorithm = algorithmRepositoryJPA.findById(algorithmId)
                .orElseThrow(() -> new RuntimeException("Algorithm not found with id: " + algorithmId));

        return Edge.builder()
                .id(UUID.randomUUID())
                .sourceNode(sourceNode)
                .targetNode(targetNode)
                .algorithm(algorithm)
                .build();
    }

    public ResponseEdgeDTO fromObjectToDTO(Edge edge) {
        return ResponseEdgeDTO.builder()
                .id(edge.getId())
                .sourceNodeId(edge.getSourceNode().getId())
                .targetNodeId(edge.getTargetNode().getId())
                .algorithmId(edge.getAlgorithm().getId())
                .build();
    }
}
