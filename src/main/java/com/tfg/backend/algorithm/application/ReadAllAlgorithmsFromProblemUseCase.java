package com.tfg.backend.algorithm.application;

import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.mapper.AlgorithmMapper;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReadAllAlgorithmsFromProblemUseCase {

    private final AlgorithmMapper algorithmMapper;
    private final AlgorithmRepositoryJPA algorithmRepositoryJPA;

    public ReadAllAlgorithmsFromProblemUseCase(AlgorithmMapper algorithmMapper, AlgorithmRepositoryJPA algorithmRepositoryJPA) {
        this.algorithmMapper = algorithmMapper;
        this.algorithmRepositoryJPA = algorithmRepositoryJPA;
    }

    public List<ResponseAlgorithmDTO> execute(UUID problemId) {
        List<Algorithm> algorithms = algorithmRepositoryJPA.findByProblemId(problemId);
        return algorithms.stream()
                .map(algorithmMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
