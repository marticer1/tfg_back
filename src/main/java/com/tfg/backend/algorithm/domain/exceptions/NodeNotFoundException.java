package com.tfg.backend.algorithm.domain.exceptions;

import java.util.UUID;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(UUID id) {
        super("Node not found with id: " + id);
    }
}
