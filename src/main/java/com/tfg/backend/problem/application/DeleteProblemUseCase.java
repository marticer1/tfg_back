package com.tfg.backend.problem.application;

import com.tfg.backend.problem.domain.exceptions.ProblemNotFoundException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteProblemUseCase {

    private final ProblemRepositoryJPA problemRepositoryJPA;

    public DeleteProblemUseCase(ProblemRepositoryJPA problemRepositoryJPA) {
        this.problemRepositoryJPA = problemRepositoryJPA;
    }

    public void execute(UUID problemId) {
        if (!problemRepositoryJPA.existsById(problemId)) {
            throw new ProblemNotFoundException(problemId);
        }
        problemRepositoryJPA.deleteById(problemId);
    }
}
