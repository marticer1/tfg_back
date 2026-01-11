package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.UpdateAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.domain.exceptions.AlgorithmNotFoundException;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateAlgorithmUseCase {

    private final AlgorithmMapper algorithmMapper;
    private final AlgorithmRepositoryJPA algorithmRepositoryJPA;

    public UpdateAlgorithmUseCase(AlgorithmMapper algorithmMapper, AlgorithmRepositoryJPA algorithmRepositoryJPA) {
        this.algorithmMapper = algorithmMapper;
        this.algorithmRepositoryJPA = algorithmRepositoryJPA;
    }

    public ResponseAlgorithmDTO execute(UUID id, UpdateAlgorithmDTO updateAlgorithmDTO) {
        Algorithm algorithm = algorithmRepositoryJPA.findById(id)
                .orElseThrow(() -> new AlgorithmNotFoundException(id));

        algorithmMapper.updateFromDTO(algorithm, updateAlgorithmDTO);

        algorithm = algorithmRepositoryJPA.save(algorithm);
        return algorithmMapper.fromObjectToDTO(algorithm);
    }
}
