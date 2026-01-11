package com.tfg.backend.problem.application;

import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.dto.UpdateProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.exceptions.ProblemNotFoundException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateProblemUseCase {

    private final ProblemMapper problemMapper;
    private final ProblemRepositoryJPA problemRepositoryJPA;

    public UpdateProblemUseCase(ProblemMapper problemMapper, ProblemRepositoryJPA problemRepositoryJPA) {
        this.problemMapper = problemMapper;
        this.problemRepositoryJPA = problemRepositoryJPA;
    }

    public ResponseProblemDTO execute(UUID id, UpdateProblemDTO updateProblemDTO) {
        Problem problem = problemRepositoryJPA.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        problemMapper.updateFromDTO(problem, updateProblemDTO);

        problem = problemRepositoryJPA.save(problem);
        return problemMapper.fromObjectToDTO(problem);
    }
}
