package com.tfg.backend.algorithm.infrastructure.repositories;

import com.tfg.backend.algorithm.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlgorithmRepositoryJPA extends JpaRepository<Algorithm, UUID> {
    List<Algorithm> findByProblemId(UUID problemId);
}
