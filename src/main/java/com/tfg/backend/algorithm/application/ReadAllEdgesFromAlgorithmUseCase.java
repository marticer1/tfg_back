package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.ResponseEdgeDTO;
import com.tfg.backend.algorithm.application.mapper.EdgeMapper;
import com.tfg.backend.algorithm.domain.Edge;
import com.tfg.backend.algorithm.infrastructure.repositories.EdgeRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReadAllEdgesFromAlgorithmUseCase {

    private final EdgeMapper edgeMapper;
    private final EdgeRepositoryJPA edgeRepositoryJPA;

    public ReadAllEdgesFromAlgorithmUseCase(EdgeMapper edgeMapper, EdgeRepositoryJPA edgeRepositoryJPA) {
        this.edgeMapper = edgeMapper;
        this.edgeRepositoryJPA = edgeRepositoryJPA;
    }

    public List<ResponseEdgeDTO> execute(UUID algorithmId) {
        List<Edge> edges = edgeRepositoryJPA.findByAlgorithmId(algorithmId);
        return edges.stream()
                .map(edgeMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
