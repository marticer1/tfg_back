package com.tfg.backend.algorithm.domain;

import com.tfg.backend.problem.domain.Problem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Algorithm {
    @Id
    private UUID id;
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    @NotNull(message = "Color cannot be null")
    private Color color;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File file;
    
    @ManyToOne
    @JoinColumn(name = "problem_id")
    @NotNull(message = "Problem cannot be null")
    private Problem problem;
    
    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Node> nodes = new ArrayList<>();
    
    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Edge> edges = new ArrayList<>();
    
    // Statistics fields
    @Builder.Default
    private Integer nodeCount = 0;
    
    @Builder.Default
    private Integer edgeCount = 0;
    
    @Builder.Default
    private Integer componentCount = 0;
}
