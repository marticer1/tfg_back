package com.tfg.backend.algorithm.domain.exceptions;

import java.util.UUID;

public class AlgorithmAlreadyExistsException extends RuntimeException {
    public AlgorithmAlreadyExistsException(UUID id) {
        super("Algorithm already exists with id: " + id);
    }
}
