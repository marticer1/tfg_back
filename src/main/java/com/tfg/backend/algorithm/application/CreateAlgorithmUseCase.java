package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.RegistrationAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.exceptions.AlgorithmAlreadyExistsException;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CreateAlgorithmUseCase {

    private final AlgorithmMapper algorithmMapper;
    private final AlgorithmRepositoryJPA algorithmRepositoryJPA;
    private final CreateNodeUseCase createNodeUseCase;

    public CreateAlgorithmUseCase(AlgorithmMapper algorithmMapper, 
                                  AlgorithmRepositoryJPA algorithmRepositoryJPA,
                                  CreateNodeUseCase createNodeUseCase) {
        this.algorithmMapper = algorithmMapper;
        this.algorithmRepositoryJPA = algorithmRepositoryJPA;
        this.createNodeUseCase = createNodeUseCase;
    }

    public ResponseAlgorithmDTO execute(RegistrationAlgorithmDTO registrationAlgorithmDTO, UUID problemId) {
        Algorithm algorithm = algorithmMapper.fromDTOtoObject(registrationAlgorithmDTO, problemId);

        if (algorithmRepositoryJPA.existsById(algorithm.getId())) {
            throw new AlgorithmAlreadyExistsException(algorithm.getId());
        }

        algorithm = algorithmRepositoryJPA.save(algorithm);
        
        // Create nodes if they exist
        if (registrationAlgorithmDTO.getNodes() != null && !registrationAlgorithmDTO.getNodes().isEmpty()) {
            for (var nodeDTO : registrationAlgorithmDTO.getNodes()) {
                createNodeUseCase.execute(nodeDTO, algorithm.getId());
            }
        }

        return algorithmMapper.fromObjectToDTO(algorithm);
    }
}
