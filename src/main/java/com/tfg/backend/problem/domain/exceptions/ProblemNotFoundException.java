package com.tfg.backend.problem.domain.exceptions;

import java.util.UUID;

public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(UUID id) {
        super("Problem with id " + id + " not found");
    }
}
