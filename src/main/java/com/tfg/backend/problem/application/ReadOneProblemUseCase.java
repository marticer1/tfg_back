package com.tfg.backend.problem.application;

import com.tfg.backend.algorithm.application.ReadAllAlgorithmsFromProblemUseCase;
import com.tfg.backend.algorithm.application.ReadAllEdgesFromAlgorithmUseCase;
import com.tfg.backend.algorithm.application.ReadAllNodesFromAlgorithmUseCase;
import com.tfg.backend.algorithm.application.dto.ResponseAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.ResponseEdgeDTO;
import com.tfg.backend.algorithm.application.dto.ResponseNodeDTO;
import com.tfg.backend.problem.application.dto.ResponseProblemDTO;
import com.tfg.backend.problem.application.mapper.ProblemMapper;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.domain.exceptions.ProblemNotFoundException;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReadOneProblemUseCase {

    private final ProblemMapper problemMapper;
    private final ProblemRepositoryJPA problemRepositoryJPA;
    private final ReadAllAlgorithmsFromProblemUseCase readAllAlgorithmsFromProblemUseCase;
    private final ReadAllNodesFromAlgorithmUseCase readAllNodesFromAlgorithmUseCase;
    private final ReadAllEdgesFromAlgorithmUseCase readAllEdgesFromAlgorithmUseCase;

    public ReadOneProblemUseCase(ProblemMapper problemMapper, 
                                 ProblemRepositoryJPA problemRepositoryJPA,
                                 ReadAllAlgorithmsFromProblemUseCase readAllAlgorithmsFromProblemUseCase,
                                 ReadAllNodesFromAlgorithmUseCase readAllNodesFromAlgorithmUseCase,
                                 ReadAllEdgesFromAlgorithmUseCase readAllEdgesFromAlgorithmUseCase) {
        this.problemMapper = problemMapper;
        this.problemRepositoryJPA = problemRepositoryJPA;
        this.readAllAlgorithmsFromProblemUseCase = readAllAlgorithmsFromProblemUseCase;
        this.readAllNodesFromAlgorithmUseCase = readAllNodesFromAlgorithmUseCase;
        this.readAllEdgesFromAlgorithmUseCase = readAllEdgesFromAlgorithmUseCase;
    }

    public ResponseProblemDTO execute(UUID problemId) {
        Problem problem = problemRepositoryJPA.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));
        
        ResponseProblemDTO responseProblemDTO = problemMapper.fromObjectToDTO(problem);
        
        // Get all algorithms for this problem
        List<ResponseAlgorithmDTO> algorithms = readAllAlgorithmsFromProblemUseCase.execute(problemId);
        
        // For each algorithm, get its nodes and edges
        for (ResponseAlgorithmDTO algorithm : algorithms) {
            List<ResponseNodeDTO> nodes = readAllNodesFromAlgorithmUseCase.execute(algorithm.getId());
            List<ResponseEdgeDTO> edges = readAllEdgesFromAlgorithmUseCase.execute(algorithm.getId());
            algorithm.setNodes(nodes);
            algorithm.setEdges(edges);
        }
        
        responseProblemDTO.setAlgorithms(algorithms);
        
        return responseProblemDTO;
    }
}
