package com.tfg.backend.problem.infrastructure.controllers;

import com.tfg.backend.problem.application.CreateProblemUseCase;
import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProblemPostController {

    private final CreateProblemUseCase createProblemUseCase;

    public ProblemPostController(CreateProblemUseCase createProblemUseCase) {
        this.createProblemUseCase = createProblemUseCase;
    }

    @PostMapping("/problem-collections/{collectionId}/problems")
    public ResponseEntity<ResponseProblemDTO> createProblem(
            @PathVariable UUID collectionId,
            @Valid @RequestBody RegistrationProblemDTO registrationProblemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createProblemUseCase.execute(registrationProblemDTO, collectionId));
    }
}
