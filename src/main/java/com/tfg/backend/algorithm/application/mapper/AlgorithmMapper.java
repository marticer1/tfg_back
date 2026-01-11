package com.tfg.backend.algorithm.application.mapper;

import com.tfg.backend.algorithm.application.dto.*;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.File;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AlgorithmMapper {

    private final ProblemRepositoryJPA problemRepositoryJPA;

    public AlgorithmMapper(ProblemRepositoryJPA problemRepositoryJPA) {
        this.problemRepositoryJPA = problemRepositoryJPA;
    }

    public Algorithm fromDTOtoObject(RegistrationAlgorithmDTO dto, UUID problemId) {
        Problem problem = problemRepositoryJPA.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));

        Algorithm algorithm = Algorithm.builder()
                .id(UUID.randomUUID())
                .name(dto.getName())
                .color(dto.getColor())
                .problem(problem)
                .build();

        if (dto.getFile() != null) {
            File file = File.builder()
                    .id(UUID.randomUUID())
                    .fileName(dto.getFile().getFileName())
                    .fileType(dto.getFile().getFileType())
                    .content(dto.getFile().getContent())
                    .build();
            algorithm.setFile(file);
        }

        return algorithm;
    }

    public ResponseAlgorithmDTO fromObjectToDTO(Algorithm algorithm) {
        ResponseAlgorithmDTO.ResponseAlgorithmDTOBuilder builder = ResponseAlgorithmDTO.builder()
                .id(algorithm.getId())
                .name(algorithm.getName())
                .color(algorithm.getColor())
                .problemId(algorithm.getProblem().getId())
                .nodeCount(algorithm.getNodeCount())
                .edgeCount(algorithm.getEdgeCount())
                .componentCount(algorithm.getComponentCount());

        if (algorithm.getFile() != null) {
            File file = algorithm.getFile();
            builder.file(FileDTO.builder()
                    .id(file.getId())
                    .fileName(file.getFileName())
                    .fileType(file.getFileType())
                    .content(file.getContent())
                    .build());
        }

        return builder.build();
    }

    public void updateFromDTO(Algorithm algorithm, UpdateAlgorithmDTO updateAlgorithmDTO) {
        algorithm.setName(updateAlgorithmDTO.getName());
        algorithm.setColor(updateAlgorithmDTO.getColor());
    }
}
