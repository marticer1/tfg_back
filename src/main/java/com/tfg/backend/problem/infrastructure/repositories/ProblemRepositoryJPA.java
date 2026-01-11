package com.tfg.backend.problem.infrastructure.repositories;

import com.tfg.backend.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemRepositoryJPA extends JpaRepository<Problem, UUID> {
    List<Problem> findByProblemCollectionId(UUID problemCollectionId);
}
