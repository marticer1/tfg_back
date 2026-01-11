package com.tfg.backend.problem.infrastructure.controllers;

import com.tfg.backend.problem.application.UpdateProblemUseCase;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.dto.UpdateProblemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProblemPutController {

    private final UpdateProblemUseCase updateProblemUseCase;

    public ProblemPutController(UpdateProblemUseCase updateProblemUseCase) {
        this.updateProblemUseCase = updateProblemUseCase;
    }

    @PutMapping("/problems/{id}")
    public ResponseEntity<ResponseProblemDTO> updateProblem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProblemDTO updateProblemDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(updateProblemUseCase.execute(id, updateProblemDTO));
    }
}
