package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.tfg.backend.problemCollection.application.DeleteProblemCollectionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProblemCollectionDeleteController {

    private final DeleteProblemCollectionUseCase deleteProblemCollectionUseCase;

    public ProblemCollectionDeleteController(DeleteProblemCollectionUseCase deleteProblemCollectionUseCase) {
        this.deleteProblemCollectionUseCase = deleteProblemCollectionUseCase;
    }

    @DeleteMapping("/problem-collections/{id}")
    public ResponseEntity<Void> deleteProblemCollection(@PathVariable UUID id){
        deleteProblemCollectionUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
