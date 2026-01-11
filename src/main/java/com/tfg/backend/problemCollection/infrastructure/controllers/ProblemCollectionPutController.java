package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.tfg.backend.problemCollection.application.UpdateProblemCollectionUseCase;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
public class ProblemCollectionPutController {

    private final UpdateProblemCollectionUseCase updateProblemCollectionUseCase;

    public ProblemCollectionPutController(UpdateProblemCollectionUseCase updateProblemCollectionUseCase) {
        this.updateProblemCollectionUseCase = updateProblemCollectionUseCase;
    }

    @PutMapping("/problem-collections/{id}")
    public ResponseEntity<ResponseProblemCollectionDTO> updateProblemCollection
            (@PathVariable UUID id, @Valid @RequestBody UpdateProblemCollectionDTO updateProblemCollectionDTO){
        return ResponseEntity.status(HttpStatus.OK).body(updateProblemCollectionUseCase.execute(id, updateProblemCollectionDTO));
    }
}
