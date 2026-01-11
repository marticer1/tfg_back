package com.tfg.backend.problem;

import com.tfg.backend.problem.application.dto.*;
import com.tfg.backend.problem.domain.*;
import com.tfg.backend.problemCollection.ProblemCollectionMother;

import java.util.UUID;

public class ProblemMother {

    public static DiscreteProblem validDiscreteProblem() {
        DiscreteProblem problem = new DiscreteProblem();
        problem.setId(UUID.randomUUID());
        problem.setName("Test Discrete Problem");
        problem.setColor(java.awt.Color.BLUE);
        problem.setColorStart(java.awt.Color.GREEN);
        problem.setColorEnd(java.awt.Color.RED);
        problem.setStartShape(Shape.ROUND);
        problem.setEndShape(Shape.STAR);
        problem.setDefaultShape(Shape.SQUARE);
        problem.setValueBestKnownSolution(100);
        problem.setNumberRuns(50);
        problem.setVertexSize(1.5);
        problem.setArrowSize(0.8);
        problem.setTreeLayout(true);
        problem.setMaximization(false);
        problem.setProblemCollection(ProblemCollectionMother.validProblemCollection());
        
        StandardPartitioning strategy = StandardPartitioning.builder()
                .id(UUID.randomUUID())
                .hypercube(4)
                .minBound(0)
                .maxBound(100)
                .numberDimension(3)
                .build();
        problem.setStandardPartitioning(strategy);
        
        return problem;
    }

    public static ContinuousProblem validContinuousProblem() {
        ContinuousProblem problem = new ContinuousProblem();
        problem.setId(UUID.randomUUID());
        problem.setName("Test Continuous Problem");
        problem.setColor(java.awt.Color.RED);
        problem.setColorStart(java.awt.Color.YELLOW);
        problem.setColorEnd(java.awt.Color.ORANGE);
        problem.setStartShape(Shape.TRIANGLE);
        problem.setEndShape(Shape.SQUARE);
        problem.setDefaultShape(Shape.ROUND);
        problem.setValueBestKnownSolution(200);
        problem.setNumberRuns(100);
        problem.setVertexSize(2.0);
        problem.setArrowSize(1.0);
        problem.setTreeLayout(false);
        problem.setMaximization(true);
        problem.setProblemCollection(ProblemCollectionMother.validProblemCollection());
        
        AgglomerativeClustering strategy = AgglomerativeClustering.builder()
                .id(UUID.randomUUID())
                .clusterSize(5.5)
                .volumeSize(10.0)
                .distance(DistanceType.EUCLIDEAN)
                .build();
        problem.setAgglomerativeClustering(strategy);
        
        return problem;
    }

    public static RegistrationProblemDTO validRegistrationDiscreteProblemDTO() {
        StandardPartitioningDTO strategyDTO = StandardPartitioningDTO.builder()
                .hypercube(4)
                .minBound(0)
                .maxBound(100)
                .numberDimension(3)
                .build();
        
        return RegistrationProblemDTO.builder()
                .name("Test Discrete Problem")
                .color(java.awt.Color.BLUE)
                .colorStart(java.awt.Color.GREEN)
                .colorEnd(java.awt.Color.RED)
                .startShape(Shape.ROUND)
                .endShape(Shape.STAR)
                .defaultShape(Shape.SQUARE)
                .problemType("DiscreteProblem")
                .valueBestKnownSolution(100)
                .numberRuns(50)
                .vertexSize(1.5)
                .arrowSize(0.8)
                .treeLayout(true)
                .isMaximization(false)
                .standardPartitioning(strategyDTO)
                .build();
    }

    public static RegistrationProblemDTO validRegistrationContinuousProblemDTO() {
        AgglomerativeClusteringDTO strategyDTO = AgglomerativeClusteringDTO.builder()
                .clusterSize(5.5)
                .volumeSize(10.0)
                .distance(DistanceType.EUCLIDEAN)
                .build();
        
        return RegistrationProblemDTO.builder()
                .name("Test Continuous Problem")
                .color(java.awt.Color.RED)
                .colorStart(java.awt.Color.YELLOW)
                .colorEnd(java.awt.Color.ORANGE)
                .startShape(Shape.TRIANGLE)
                .endShape(Shape.SQUARE)
                .defaultShape(Shape.ROUND)
                .problemType("ContinuousProblem")
                .valueBestKnownSolution(200)
                .numberRuns(100)
                .vertexSize(2.0)
                .arrowSize(1.0)
                .treeLayout(false)
                .isMaximization(true)
                .agglomerativeClustering(strategyDTO)
                .build();
    }
}
