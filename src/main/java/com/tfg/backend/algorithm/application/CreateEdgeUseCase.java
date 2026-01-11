package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.RegistrationEdgeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseEdgeDTO;
import com.tfg.backend.algorithm.application.mapper.EdgeMapper;
import com.tfg.backend.algorithm.domain.Edge;
import com.tfg.backend.algorithm.infrastructure.repositories.EdgeRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CreateEdgeUseCase {

    private final EdgeMapper edgeMapper;
    private final EdgeRepositoryJPA edgeRepositoryJPA;

    public CreateEdgeUseCase(EdgeMapper edgeMapper, EdgeRepositoryJPA edgeRepositoryJPA) {
        this.edgeMapper = edgeMapper;
        this.edgeRepositoryJPA = edgeRepositoryJPA;
    }

    public ResponseEdgeDTO execute(RegistrationEdgeDTO registrationEdgeDTO, UUID algorithmId) {
        Edge edge = edgeMapper.fromDTOtoObject(registrationEdgeDTO, algorithmId);
        edge = edgeRepositoryJPA.save(edge);
        return edgeMapper.fromObjectToDTO(edge);
    }
}
