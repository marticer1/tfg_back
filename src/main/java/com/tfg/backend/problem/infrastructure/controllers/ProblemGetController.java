package com.tfg.backend.problem.infrastructure.controllers;

import com.tfg.backend.problem.application.ReadAllProblemsFromCollectionUseCase;
import com.tfg.backend.problem.application.ReadOneProblemUseCase;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ProblemGetController {

    private final ReadAllProblemsFromCollectionUseCase readAllProblemsFromCollectionUseCase;
    private final ReadOneProblemUseCase readOneProblemUseCase;

    public ProblemGetController(ReadAllProblemsFromCollectionUseCase readAllProblemsFromCollectionUseCase, 
                                ReadOneProblemUseCase readOneProblemUseCase) {
        this.readAllProblemsFromCollectionUseCase = readAllProblemsFromCollectionUseCase;
        this.readOneProblemUseCase = readOneProblemUseCase;
    }

    @GetMapping("/problem-collections/{collectionId}/problems")
    public ResponseEntity<List<ResponseProblemDTO>> getAllProblemsFromCollection(@PathVariable UUID collectionId) {
        return ResponseEntity.ok(readAllProblemsFromCollectionUseCase.execute(collectionId));
    }

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<ResponseProblemDTO> getProblemById(@PathVariable UUID problemId) {
        return ResponseEntity.ok(readOneProblemUseCase.execute(problemId));
    }
}
