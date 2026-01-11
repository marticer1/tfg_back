package com.tfg.backend.problem.application;

import com.tfg.backend.algorithm.application.CreateAlgorithmUseCase;
import com.tfg.backend.api.StnGeneratorService;
import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.exceptions.ProblemAlreadyExistsException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import com.tfg.backend.visualization.VisualizationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class CreateProblemUseCase {

    private final ProblemMapper problemMapper;
    private final ProblemRepositoryJPA problemRepositoryJPA;
    private final CreateAlgorithmUseCase createAlgorithmUseCase;
    private final StnGeneratorService stnGeneratorService;
    private final VisualizationService visualizationService;

    public CreateProblemUseCase(ProblemMapper problemMapper,
                                ProblemRepositoryJPA problemRepositoryJPA,
                                CreateAlgorithmUseCase createAlgorithmUseCase,
                                StnGeneratorService stnGeneratorService,
                                VisualizationService visualizationService) {
        this.problemMapper = problemMapper;
        this.problemRepositoryJPA = problemRepositoryJPA;
        this.createAlgorithmUseCase = createAlgorithmUseCase;
        this.stnGeneratorService = stnGeneratorService;
        this.visualizationService = visualizationService;
    }

    public ResponseProblemDTO execute(RegistrationProblemDTO registrationProblemDTO, UUID problemCollectionId) {
        Problem problem = problemMapper.fromDTOtoObject(registrationProblemDTO, problemCollectionId);

        if (problemRepositoryJPA.existsById(problem.getId())) {
            throw new ProblemAlreadyExistsException(problem.getId());
        }

        problem = problemRepositoryJPA.save(problem);

        // Crear algoritmos si existen
        if (registrationProblemDTO.getAlgorithms() != null && !registrationProblemDTO.getAlgorithms().isEmpty()) {
            for (var algorithmDTO : registrationProblemDTO.getAlgorithms()) {
                createAlgorithmUseCase.execute(algorithmDTO, problem.getId());
            }
        }

        // Generar visualización STN SIN endpoint HTTP (llamada directa a Python/R)
        try {
            Path[] paths = stnGeneratorService.generateFromRegistration(registrationProblemDTO, problem.getId());
            if (paths != null && paths.length >= 2) {
                Path pdfPath = paths[0];
                Path jsonPath = paths[1];
                
                // Process JSON and persist nodes
                // Use tree layout preference from the problem, default to KK (false)
                boolean useKKLayout = !Boolean.TRUE.equals(registrationProblemDTO.getTreeLayout());
                visualizationService.processAndPersistVisualization(jsonPath, problem.getId(), useKKLayout);
                
                // Here you can also: save the PDF in your storage, associate it with the problem, etc.
                // e.g. Files.copy(pdfPath, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            // Log and don't interrupt problem creation
            System.err.println("STN visualization generation or node persistence failed: " + e.getMessage());
            e.printStackTrace();
        }

        return problemMapper.fromObjectToDTO(problem);
    }
}