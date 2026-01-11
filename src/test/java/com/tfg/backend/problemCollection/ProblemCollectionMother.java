package com.tfg.backend.problemCollection;

import com.tfg.backend.problemCollection.application.dto.RegistrationProblemCollectionDTO;
import com.tfg.backend.problemCollection.application.dto.UpdateProblemCollectionDTO;
import com.tfg.backend.problemCollection.domain.ProblemCollection;

import java.awt.*;
import java.util.UUID;

public class ProblemCollectionMother {
    public static ProblemCollection validProblemCollection(){
        return ProblemCollection.builder()
                .id(UUID.randomUUID())
                .name("Test Collection")
                .color(Color.BLACK)
                .build();
    }

    public static RegistrationProblemCollectionDTO validRegistrationProblemCollectionDTO(){
        return RegistrationProblemCollectionDTO.builder()
                .name("Test Collection")
                .color(Color.BLACK)
                .build();
    }

    public static UpdateProblemCollectionDTO validUpdateProblemCollectionDTO(){
        return UpdateProblemCollectionDTO.builder()
                .name("Updated Collection")
                .color(Color.BLUE)
                .build();
    }
}
