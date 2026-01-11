package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.RegistrationNodeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.algorithm.application.mapper.NodeMapper;
import com.tfg.backend.algorithm.domain.Node;
import com.tfg.backend.algorithm.domain.exceptions.NodeAlreadyExistsException;
import com.tfg.backend.algorithm.infrastructure.repositories.NodeRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CreateNodeUseCase {

    private final NodeMapper nodeMapper;
    private final NodeRepositoryJPA nodeRepositoryJPA;

    public CreateNodeUseCase(NodeMapper nodeMapper, NodeRepositoryJPA nodeRepositoryJPA) {
        this.nodeMapper = nodeMapper;
        this.nodeRepositoryJPA = nodeRepositoryJPA;
    }

    public ResponseNodeDTO execute(RegistrationNodeDTO registrationNodeDTO, UUID algorithmId) {
        Node node = nodeMapper.fromDTOtoObject(registrationNodeDTO, algorithmId);

        if (nodeRepositoryJPA.existsById(node.getId())) {
            throw new NodeAlreadyExistsException(node.getId());
        }

        node = nodeRepositoryJPA.save(node);
        return nodeMapper.fromObjectToDTO(node);
    }
}
