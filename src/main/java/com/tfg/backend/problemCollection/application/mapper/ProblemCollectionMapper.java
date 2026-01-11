package com.tfg.backend.problemCollection.application.mapper;

import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.ResponseProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProblemCollectionMapper {

    public ProblemCollection fromDTOtoObject(RegistrationProblemCollectionDTO registrationProblemCollectionDTO) {
        return ProblemCollection.builder()
                .id(UUID.randomUUID())
                .name(registrationProblemCollectionDTO.getName())
                .color(registrationProblemCollectionDTO.getColor())
                .build();
    }

    public ResponseProblemCollectionDTO fromObjectToDTO(ProblemCollection problemCollection) {
        return ResponseProblemCollectionDTO.builder()
                .id(problemCollection.getId())
                .name(problemCollection.getName())
                .color(problemCollection.getColor())
                .build();
    }

    public void updateFromDTO(ProblemCollection problemCollection, UpdateProblemCollectionDTO updateProblemCollectionDTO) {
        problemCollection.setName(updateProblemCollectionDTO.getName());
        problemCollection.setColor(updateProblemCollectionDTO.getColor());
    }
}
