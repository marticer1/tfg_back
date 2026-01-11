package com.tfg.backend.algorithm.application.mapper;

import com.tfg.backend.algorithm.application.dto.Position3DDTO;
import com.tfg.backend.algorithm.application.dto.RegistrationNodeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.domain.Position3D;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NodeMapper {

    private final AlgorithmRepositoryJPA algorithmRepositoryJPA;

    public NodeMapper(AlgorithmRepositoryJPA algorithmRepositoryJPA) {
        this.algorithmRepositoryJPA = algorithmRepositoryJPA;
    }

    public Node fromDTOtoObject(RegistrationNodeDTO dto, UUID algorithmId) {
        Algorithm algorithm = algorithmRepositoryJPA.findById(algorithmId)
                .orElseThrow(() -> new RuntimeException("Algorithm not found with id: " + algorithmId));

        Position3D position = Position3D.builder()
                .x(dto.getPosition().getX())
                .y(dto.getPosition().getY())
                .z(dto.getPosition().getZ())
                .build();

        return Node.builder()
                .id(UUID.randomUUID())
                .type(dto.getType())
                .position(position)
                .algorithm(algorithm)
                .build();
    }

    public ResponseNodeDTO fromObjectToDTO(Node node) {
        Position3DDTO positionDTO = Position3DDTO.builder()
                .x(node.getPosition().getX())
                .y(node.getPosition().getY())
                .z(node.getPosition().getZ())
                .build();

        return ResponseNodeDTO.builder()
                .id(node.getId())
                .type(node.getType())
                .position(positionDTO)
                .algorithmId(node.getAlgorithm().getId())
                .build();
    }
}
