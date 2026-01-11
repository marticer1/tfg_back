package com.tfg.backend.algorithm.infrastructure.controllers;

import com.tfg.backend.algorithm.application.UpdateAlgorithmUseCase;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.UpdateAlgorithmDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AlgorithmPutController {

    private final UpdateAlgorithmUseCase updateAlgorithmUseCase;

    public AlgorithmPutController(UpdateAlgorithmUseCase updateAlgorithmUseCase) {
        this.updateAlgorithmUseCase = updateAlgorithmUseCase;
    }

    @PutMapping("/algorithms/{id}")
    public ResponseEntity<ResponseAlgorithmDTO> updateAlgorithm(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAlgorithmDTO updateAlgorithmDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(updateAlgorithmUseCase.execute(id, updateAlgorithmDTO));
    }
}
