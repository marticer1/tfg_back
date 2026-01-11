package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReadAllProblemCollectionsUseCase {

    private final ProblemCollectionMapper problemCollectionMapper;
    private final ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    public ReadAllProblemCollectionsUseCase(ProblemCollectionMapper problemCollectionMapper, ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA) {
        this.problemCollectionMapper = problemCollectionMapper;
        this.problemCollectionRepositoryJPA = problemCollectionRepositoryJPA;
    }

    public List<ResponseProblemCollectionDTO> execute() {
        List<ProblemCollection> problemCollections = problemCollectionRepositoryJPA.findAll();
        return problemCollections.stream()
                .map(problemCollectionMapper::fromObjectToDTO)
                .collect(Collectors.toList());
    }
}
