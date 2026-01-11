package com.tfg.backend.problemCollection.domain.exceptions;

import java.util.UUID;

public class ProblemCollectionAlreadyExistException extends RuntimeException{
    public ProblemCollectionAlreadyExistException(UUID id) {
        super("Business with id " + id + " already exists");
    }
}
