package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.application.mapper.NodeMapper;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReadAllNodesFromAlgorithmUseCase {

    private final NodeMapper nodeMapper;
    private final NodeRepositoryJPA nodeRepositoryJPA;

    public ReadAllNodesFromAlgorithmUseCase(NodeMapper nodeMapper, NodeRepositoryJPA nodeRepositoryJPA) {
        this.nodeMapper = nodeMapper;
        this.nodeRepositoryJPA = nodeRepositoryJPA;
    }

    public List<ResponseNodeDTO> execute(UUID algorithmId) {
        List<Node> nodes = nodeRepositoryJPA.findByAlgorithmId(algorithmId);
        return nodes.stream()
                .map(nodeMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
