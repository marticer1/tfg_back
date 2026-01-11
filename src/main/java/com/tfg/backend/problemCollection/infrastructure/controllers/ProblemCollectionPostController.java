package com.tfg.backend.problemCollection.infrastructure.controllers;

import com.tfg.backend.problemCollection.application.CreateProblemCollectionUseCase;
import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
public class ProblemCollectionPostController {

    private final CreateProblemCollectionUseCase createProblemCollectionUseCase;

    public ProblemCollectionPostController(CreateProblemCollectionUseCase createProblemCollectionUseCase) {
        this.createProblemCollectionUseCase = createProblemCollectionUseCase;
    }

    @PostMapping("/problem-collections")
    public ResponseEntity<ResponseProblemCollectionDTO> createProblemCollection
            (@Valid @RequestBody RegistrationProblemCollectionDTO registrationProblemCollectionDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(createProblemCollectionUseCase.execute(registrationProblemCollectionDTO));
    }
}
