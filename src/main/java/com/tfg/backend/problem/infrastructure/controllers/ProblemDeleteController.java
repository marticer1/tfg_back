package com.tfg.backend.problem.infrastructure.controllers;

import com.tfg.backend.problem.application.DeleteProblemUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProblemDeleteController {

    private final DeleteProblemUseCase deleteProblemUseCase;

    public ProblemDeleteController(DeleteProblemUseCase deleteProblemUseCase) {
        this.deleteProblemUseCase = deleteProblemUseCase;
    }

    @DeleteMapping("/problems/{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable UUID problemId) {
        deleteProblemUseCase.execute(problemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
