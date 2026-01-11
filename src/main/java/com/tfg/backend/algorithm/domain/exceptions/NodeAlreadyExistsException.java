package com.tfg.backend.algorithm.domain.exceptions;

import java.util.UUID;

public class NodeAlreadyExistsException extends RuntimeException {
    public NodeAlreadyExistsException(UUID id) {
        super("Node already exists with id: " + id);
    }
}
