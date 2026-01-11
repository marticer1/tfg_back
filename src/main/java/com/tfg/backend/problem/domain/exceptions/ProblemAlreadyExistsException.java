package com.tfg.backend.problem.domain.exceptions;

import java.util.UUID;

public class ProblemAlreadyExistsException extends RuntimeException {
    public ProblemAlreadyExistsException(UUID id) {
        super("Problem with id " + id + " already exists");
    }
}
