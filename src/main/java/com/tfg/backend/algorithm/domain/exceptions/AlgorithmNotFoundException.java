package com.tfg.backend.algorithm.domain.exceptions;

import java.util.UUID;

public class AlgorithmNotFoundException extends RuntimeException {
    public AlgorithmNotFoundException(UUID id) {
        super("Algorithm not found with id: " + id);
    }
}
