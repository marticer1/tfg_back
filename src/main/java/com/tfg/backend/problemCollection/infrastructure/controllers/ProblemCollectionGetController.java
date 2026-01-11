package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.tfg.backend.problemCollection.application.ReadAllProblemCollectionsUseCase;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProblemCollectionGetController {

    private final ReadAllProblemCollectionsUseCase readAllProblemCollectionsUseCase;

    public ProblemCollectionGetController(ReadAllProblemCollectionsUseCase readAllProblemCollectionsUseCase) {
        this.readAllProblemCollectionsUseCase = readAllProblemCollectionsUseCase;
    }

    @GetMapping("/problem-collections")
    public ResponseEntity<List<ResponseProblemCollectionDTO>> getAllProblemCollections() {
        return ResponseEntity.ok(readAllProblemCollectionsUseCase.execute());
    }
}
