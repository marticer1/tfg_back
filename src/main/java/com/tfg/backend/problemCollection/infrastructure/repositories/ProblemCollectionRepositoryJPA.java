package com.tfg.backend.problemCollection.infrastructure.repositories;

import com.tfg.backend.problemCollection.domain.ProblemCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProblemCollectionRepositoryJPA extends JpaRepository<ProblemCollection, UUID> {
}
