package com.tfg.backend.problem.application;

import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReadAllProblemsFromCollectionUseCase {

    private final ProblemMapper problemMapper;
    private final ProblemRepositoryJPA problemRepositoryJPA;

    public ReadAllProblemsFromCollectionUseCase(ProblemMapper problemMapper, ProblemRepositoryJPA problemRepositoryJPA) {
        this.problemMapper = problemMapper;
        this.problemRepositoryJPA = problemRepositoryJPA;
    }

    public List<ResponseProblemDTO> execute(UUID problemCollectionId) {
        List<Problem> problems = problemRepositoryJPA.findByProblemCollectionId(problemCollectionId);
        return problems.stream()
                .map(problemMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
