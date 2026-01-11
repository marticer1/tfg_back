package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.domain.exceptions.ProblemCollectionNotFoundException;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateProblemCollectionUseCase {

    private final ProblemCollectionMapper problemCollectionMapper;
    private final ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    public UpdateProblemCollectionUseCase(ProblemCollectionMapper problemCollectionMapper, ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA) {
        this.problemCollectionMapper = problemCollectionMapper;
        this.problemCollectionRepositoryJPA = problemCollectionRepositoryJPA;
    }

    public ResponseProblemCollectionDTO execute(UUID id, UpdateProblemCollectionDTO updateProblemCollectionDTO){
        ProblemCollection problemCollection = problemCollectionRepositoryJPA.findById(id)
                .orElseThrow(() -> new ProblemCollectionNotFoundException(id));

        problemCollectionMapper.updateFromDTO(problemCollection, updateProblemCollectionDTO);

        problemCollection = problemCollectionRepositoryJPA.save(problemCollection);
        return problemCollectionMapper.fromObjectToDTO(problemCollection);
    }
}
