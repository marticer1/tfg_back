package com.tfg.backend.problemCollection.application;

import com.tfg.backend.problemCollection.domain.exceptions.ProblemCollectionNotFoundException;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteProblemCollectionUseCase {

    private final ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    public DeleteProblemCollectionUseCase(ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA) {
        this.problemCollectionRepositoryJPA = problemCollectionRepositoryJPA;
    }

    public void execute(UUID id){
        if(!problemCollectionRepositoryJPA.existsById(id)){
            return;
        }

        problemCollectionRepositoryJPA.deleteById(id);
    }
}
