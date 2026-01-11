package com.tfg.backend.problem.application.mapper;

import com.tfg.backend.problem.application.dto.*;
import com.tfg.backend.problem.domain.*;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProblemMapper {

    private final ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    public ProblemMapper(ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA) {
        this.problemCollectionRepositoryJPA = problemCollectionRepositoryJPA;
    }

    public Problem fromDTOtoObject(RegistrationProblemDTO dto, UUID problemCollectionId) {
        ProblemCollection problemCollection = problemCollectionRepositoryJPA.findById(problemCollectionId)
                .orElseThrow(() -> new RuntimeException("Problem collection not found with id: " + problemCollectionId));

        Problem problem;
        if ("DiscreteProblem".equalsIgnoreCase(dto.getProblemType())) {
            problem = new DiscreteProblem();
        } else if ("ContinuousProblem".equalsIgnoreCase(dto.getProblemType())) {
            problem = new ContinuousProblem();
        } else {
            throw new IllegalArgumentException("Invalid problem type: " + dto.getProblemType());
        }

        problem.setId(UUID.randomUUID());
        problem.setName(dto.getName());
        problem.setColor(dto.getColor());
        problem.setColorStart(dto.getColorStart());
        problem.setColorEnd(dto.getColorEnd());
        problem.setStartShape(dto.getStartShape());
        problem.setEndShape(dto.getEndShape());
        problem.setDefaultShape(dto.getDefaultShape());
        problem.setValueBestKnownSolution(dto.getValueBestKnownSolution());
        problem.setNumberRuns(dto.getNumberRuns());
        problem.setVertexSize(dto.getVertexSize());
        problem.setArrowSize(dto.getArrowSize());
        problem.setTreeLayout(dto.getTreeLayout());
        problem.setMaximization(true);
        problem.setProblemCollection(problemCollection);

        // Map strategies
        if (dto.getStandardPartitioning() != null) {
            StandardPartitioning strategy = StandardPartitioning.builder()
                    .id(UUID.randomUUID())
                    .hypercube(dto.getStandardPartitioning().getHypercube())
                    .minBound(dto.getStandardPartitioning().getMinBound())
                    .maxBound(dto.getStandardPartitioning().getMaxBound())
                    .numberDimension(dto.getStandardPartitioning().getNumberDimension())
                    .build();
            problem.setStandardPartitioning(strategy);
        }

        if (dto.getAgglomerativeClustering() != null) {
            AgglomerativeClustering strategy = AgglomerativeClustering.builder()
                    .id(UUID.randomUUID())
                    .clusterSize(dto.getAgglomerativeClustering().getClusterSize())
                    .volumeSize(dto.getAgglomerativeClustering().getVolumeSize())
                    .distance(dto.getAgglomerativeClustering().getDistance())
                    .build();
            problem.setAgglomerativeClustering(strategy);
        }

        if (dto.getShannonEntropy() != null) {
            ShannonEntropy strategy = ShannonEntropy.builder()
                    .id(UUID.randomUUID())
                    .partitioning(dto.getShannonEntropy().getPartitioning())
                    .build();
            problem.setShannonEntropy(strategy);
        }

        return problem;
    }

    public ResponseProblemDTO fromObjectToDTO(Problem problem) {
        ResponseProblemDTO.ResponseProblemDTOBuilder builder = ResponseProblemDTO.builder()
                .id(problem.getId())
                .name(problem.getName())
                .color(problem.getColor())
                .colorStart(problem.getColorStart())
                .colorEnd(problem.getColorEnd())
                .startShape(problem.getStartShape())
                .endShape(problem.getEndShape())
                .defaultShape(problem.getDefaultShape())
                .problemCollectionId(problem.getProblemCollection().getId())
                .problemType(problem instanceof DiscreteProblem ? "DiscreteProblem" : "ContinuousProblem")
                .valueBestKnownSolution(problem.getValueBestKnownSolution())
                .numberRuns(problem.getNumberRuns())
                .vertexSize(problem.getVertexSize())
                .arrowSize(problem.getArrowSize())
                .treeLayout(problem.isTreeLayout())
                .isMaximization(problem.isMaximization());

        // Map strategies
        if (problem.getStandardPartitioning() != null) {
            StandardPartitioning strategy = problem.getStandardPartitioning();
            builder.standardPartitioning(StandardPartitioningDTO.builder()
                    .id(strategy.getId())
                    .hypercube(strategy.getHypercube())
                    .minBound(strategy.getMinBound())
                    .maxBound(strategy.getMaxBound())
                    .numberDimension(strategy.getNumberDimension())
                    .build());
        }

        if (problem.getAgglomerativeClustering() != null) {
            AgglomerativeClustering strategy = problem.getAgglomerativeClustering();
            builder.agglomerativeClustering(AgglomerativeClusteringDTO.builder()
                    .id(strategy.getId())
                    .clusterSize(strategy.getClusterSize())
                    .volumeSize(strategy.getVolumeSize())
                    .distance(strategy.getDistance())
                    .build());
        }

        if (problem.getShannonEntropy() != null) {
            ShannonEntropy strategy = problem.getShannonEntropy();
            builder.shannonEntropy(ShannonEntropyDTO.builder()
                    .id(strategy.getId())
                    .partitioning(strategy.getPartitioning())
                    .build());
        }

        return builder.build();
    }

    public void updateFromDTO(Problem problem, UpdateProblemDTO updateProblemDTO) {
        problem.setName(updateProblemDTO.getName());
        problem.setColor(updateProblemDTO.getColor());
        problem.setColorStart(updateProblemDTO.getColorStart());
        problem.setColorEnd(updateProblemDTO.getColorEnd());
        problem.setStartShape(updateProblemDTO.getStartShape());
        problem.setEndShape(updateProblemDTO.getEndShape());
        problem.setDefaultShape(updateProblemDTO.getDefaultShape());
        problem.setVertexSize(updateProblemDTO.getVertexSize());
        problem.setArrowSize(updateProblemDTO.getArrowSize());
        if (updateProblemDTO.getIsMaximization() != null) {
            problem.setMaximization(updateProblemDTO.getIsMaximization());
        }
    }
}
