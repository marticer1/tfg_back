package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.mapper.ProblemCollectionMapper;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.domain.exceptions.ProblemCollectionAlreadyExistException;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateProblemCollectionUseCase {

    private final ProblemCollectionMapper problemCollectionMapper;
    private final ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    public CreateProblemCollectionUseCase(ProblemCollectionMapper problemCollectionMapper, ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA) {
        this.problemCollectionMapper = problemCollectionMapper;
        this.problemCollectionRepositoryJPA = problemCollectionRepositoryJPA;
    }

    public ResponseProblemCollectionDTO execute(RegistrationProblemCollectionDTO registrationProblemCollectionDTO){
        ProblemCollection problemCollection = problemCollectionMapper.fromDTOtoObject(registrationProblemCollectionDTO);

        if(problemCollectionRepositoryJPA.existsById(problemCollection.getId())){
            throw new ProblemCollectionAlreadyExistException(problemCollection.getId());
        }

        problemCollection = problemCollectionRepositoryJPA.save(problemCollection);
        return problemCollectionMapper.fromObjectToDTO(problemCollection);
    }
}
