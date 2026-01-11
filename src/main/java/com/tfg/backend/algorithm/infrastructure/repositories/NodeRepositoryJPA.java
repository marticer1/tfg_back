package com.tfg.backend.algorithm.infrastructure.repositories;

import com.tfg.backend.algorithm.domain.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NodeRepositoryJPA extends JpaRepository<Node, UUID> {
    List<Node> findByAlgorithmId(UUID algorithmId);
    int countByAlgorithmId(UUID algorithmId);
}
