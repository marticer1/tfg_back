package com.tfg.backend.algorithm;

import com.tfg.backend.algorithm.application.dto.*;
import com.tfg.backend.algorithm.domain.*;
import com.tfg.backend.problem.ProblemMother;
import com.tfg.backend.problem.domain.Problem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlgorithmMother {

    public static Algorithm validAlgorithm() {
        Problem problem = ProblemMother.validDiscreteProblem();
        
        File file = File.builder()
                .id(UUID.randomUUID())
                .fileName("algorithm.txt")
                .fileType("text/plain")
                .content("MSw3MTc5LDEwMDAwMTAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwM")
                .build();
        
        return Algorithm.builder()
                .id(UUID.randomUUID())
                .name("Test Algorithm")
                .color(Color.RED)
                .file(file)
                .problem(problem)
                .build();
    }

    public static Algorithm validAlgorithmWithoutFile() {
        Problem problem = ProblemMother.validDiscreteProblem();
        
        return Algorithm.builder()
                .id(UUID.randomUUID())
                .name("Test Algorithm")
                .color(Color.BLUE)
                .problem(problem)
                .build();
    }

    public static RegistrationAlgorithmDTO validRegistrationAlgorithmDTO() {
        FileDTO fileDTO = FileDTO.builder()
                .fileName("algorithm.txt")
                .fileType("text/plain")
                .content("MSw3MTc5LDEwMDAwMTAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwM")
                .build();
        
        List<RegistrationNodeDTO> nodes = new ArrayList<>();
        nodes.add(validRegistrationNodeDTO(NodeType.START));
        nodes.add(validRegistrationNodeDTO(NodeType.DEFAULT));
        nodes.add(validRegistrationNodeDTO(NodeType.END));
        
        return RegistrationAlgorithmDTO.builder()
                .name("Test Algorithm")
                .color(Color.RED)
                .file(fileDTO)
                .nodes(nodes)
                .build();
    }

    public static RegistrationAlgorithmDTO validRegistrationAlgorithmDTOWithoutFile() {
        return RegistrationAlgorithmDTO.builder()
                .name("Test Algorithm")
                .color(Color.BLUE)
                .build();
    }

    public static ResponseAlgorithmDTO validResponseAlgorithmDTO() {
        FileDTO fileDTO = FileDTO.builder()
                .id(UUID.randomUUID())
                .fileName("algorithm.txt")
                .fileType("text/plain")
                .content("MSw3MTc5LDEwMDAwMTAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwM")
                .build();
        
        return ResponseAlgorithmDTO.builder()
                .id(UUID.randomUUID())
                .name("Test Algorithm")
                .color(Color.RED)
                .file(fileDTO)
                .problemId(UUID.randomUUID())
                .build();
    }

    public static UpdateAlgorithmDTO validUpdateAlgorithmDTO() {
        return UpdateAlgorithmDTO.builder()
                .name("Updated Algorithm")
                .color(Color.GREEN)
                .build();
    }

    public static Node validNode() {
        Algorithm algorithm = validAlgorithmWithoutFile();
        
        Position3D position = Position3D.builder()
                .x(1.0)
                .y(2.0)
                .z(3.0)
                .build();
        
        return Node.builder()
                .id(UUID.randomUUID())
                .type(NodeType.START)
                .position(position)
                .algorithm(algorithm)
                .build();
    }

    public static RegistrationNodeDTO validRegistrationNodeDTO(NodeType type) {
        Position3DDTO positionDTO = Position3DDTO.builder()
                .x(1.0)
                .y(2.0)
                .z(3.0)
                .build();
        
        return RegistrationNodeDTO.builder()
                .type(type)
                .position(positionDTO)
                .build();
    }

    public static ResponseNodeDTO validResponseNodeDTO() {
        Position3DDTO positionDTO = Position3DDTO.builder()
                .x(1.0)
                .y(2.0)
                .z(3.0)
                .build();
        
        return ResponseNodeDTO.builder()
                .id(UUID.randomUUID())
                .type(NodeType.START)
                .position(positionDTO)
                .algorithmId(UUID.randomUUID())
                .build();
    }

    public static Edge validEdge() {
        Algorithm algorithm = validAlgorithmWithoutFile();
        Node sourceNode = validNode();
        Node targetNode = validNode();
        
        return Edge.builder()
                .id(UUID.randomUUID())
                .sourceNode(sourceNode)
                .targetNode(targetNode)
                .algorithm(algorithm)
                .build();
    }

    public static RegistrationEdgeDTO validRegistrationEdgeDTO(UUID sourceNodeId, UUID targetNodeId) {
        return RegistrationEdgeDTO.builder()
                .sourceNodeId(sourceNodeId)
                .targetNodeId(targetNodeId)
                .build();
    }

    public static ResponseEdgeDTO validResponseEdgeDTO() {
        return ResponseEdgeDTO.builder()
                .id(UUID.randomUUID())
                .sourceNodeId(UUID.randomUUID())
                .targetNodeId(UUID.randomUUID())
                .algorithmId(UUID.randomUUID())
                .build();
    }
}
