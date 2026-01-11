package com.tfg.backend.problem.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.common.ColorSerializer;
import com.tfg.backend.problem.domain.Shape;
import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ResponseProblemDTO {
    private UUID id;
    private String name;
    @JsonSerialize(using = ColorSerializer.class)
    private Color color;
    @JsonSerialize(using = ColorSerializer.class)
    private Color colorStart;
    @JsonSerialize(using = ColorSerializer.class)
    private Color colorEnd;
    private Shape startShape;
    private Shape endShape;
    private Shape defaultShape;
    private UUID problemCollectionId;
    private String problemType;
    private int valueBestKnownSolution;
    private int numberRuns;
    private double vertexSize;
    private double arrowSize;
    private boolean treeLayout;
    private boolean isMaximization;
    private StandardPartitioningDTO standardPartitioning;
    private AgglomerativeClusteringDTO agglomerativeClustering;
    private ShannonEntropyDTO shannonEntropy;
    private List<ResponseAlgorithmDTO> algorithms;
}
