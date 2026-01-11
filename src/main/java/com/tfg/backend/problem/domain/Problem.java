package com.tfg.backend.problem.domain;

import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "problem_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Problem {
    @Id
    private UUID id;

    @NotBlank(message = "Name can not be blank")
    private String name;

    @NotNull(message = "Color can not be null")
    private Color color;
    
    private Color colorStart;
    
    private Color colorEnd;
    
    @Enumerated(EnumType.STRING)
    private Shape startShape;
    
    @Enumerated(EnumType.STRING)
    private Shape endShape;
    
    @Enumerated(EnumType.STRING)
    private Shape defaultShape;
    
    @NotNull(message = "Value best known solution cannot be null")
    private int valueBestKnownSolution;
    
    @NotNull(message = "Number runs cannot be null")
    private int numberRuns;
    
    @NotNull(message = "Vertex size cannot be null")
    private double vertexSize;
    
    @NotNull(message = "Arrow size cannot be null")
    private double arrowSize;
    
    @NotNull(message = "Tree layout cannot be null")
    private boolean treeLayout;
    
    @NotNull(message = "Is maximization cannot be null")
    private boolean isMaximization;
    
    @ManyToOne
    @JoinColumn(name = "problem_collection_id")
    @NotNull(message = "Problem collection cannot be null")
    private ProblemCollection problemCollection;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "standard_partitioning_id")
    private StandardPartitioning standardPartitioning;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agglomerative_clustering_id")
    private AgglomerativeClustering agglomerativeClustering;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shannon_entropy_id")
    private ShannonEntropy shannonEntropy;
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Algorithm> algorithms = new ArrayList<>();
}
