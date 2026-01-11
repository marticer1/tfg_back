package com.tfg.backend.problem.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tfg.backend.algorithm.application.dto.RegistrationAlgorithmDTO;
import com.tfg.backend.common.ColorDeserializer;
import com.tfg.backend.problem.domain.Shape;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationProblemDTO {
    @NotBlank(message = "Name can not be blank")
    private String name;

    @NotNull (message = "Color can not be null")
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color color;
    
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color colorStart;
    
    @JsonDeserialize(using = ColorDeserializer.class)
    private Color colorEnd;
    
    private Shape startShape;
    
    private Shape endShape;
    
    private Shape defaultShape;

    @NotNull(message = "Problem type cannot be null")
    private String problemType; // "DiscreteProblem" OR "ContinuousProblem"
    
    @NotNull(message = "Value best known solution cannot be null")
    private Integer valueBestKnownSolution;
    
    @NotNull(message = "Number runs cannot be null")
    private Integer numberRuns;
    
    @NotNull(message = "Vertex size cannot be null")
    private Double vertexSize;
    
    @NotNull(message = "Arrow size cannot be null")
    private Double arrowSize;
    
    @NotNull(message = "Tree layout cannot be null")
    private Boolean treeLayout;
    
    @NotNull(message = "Is maximization cannot be null")
    private Boolean isMaximization;
    
    private StandardPartitioningDTO standardPartitioning;
    private AgglomerativeClusteringDTO agglomerativeClustering;
    private ShannonEntropyDTO shannonEntropy;
    private List<RegistrationAlgorithmDTO> algorithms;
}
