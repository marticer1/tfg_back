package com.tfg.backend.problemCollection.domain.exceptions;

import java.util.UUID;

public class ProblemCollectionNotFoundException extends RuntimeException{
    public ProblemCollectionNotFoundException(UUID id) {
        super("ProblemCollection with id " + id + " not found");
    }
}
