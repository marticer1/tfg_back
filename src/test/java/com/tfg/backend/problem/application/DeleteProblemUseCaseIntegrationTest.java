package com.tfg.backend.problem.application;

import com.tfg.backend.TestContainersConfig;
import com.tfg.backend.algorithm.domain.Algorithm;
import com.tfg.backend.algorithm.infrastructure.repositories.AlgorithmRepositoryJPA;
import com.tfg.backend.problem.domain.ContinuousProblem;
import com.tfg.backend.problem.domain.Problem;
import com.tfg.backend.problem.infrastructure.repositories.ProblemRepositoryJPA;
import com.tfg.backend.problemCollection.domain.ProblemCollection;
import com.tfg.backend.problemCollection.infrastructure.repositories.ProblemCollectionRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.awt.Color;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainersConfig.class)
class DeleteProblemUseCaseIntegrationTest {

    @Autowired
    private DeleteProblemUseCase deleteProblemUseCase;

    @Autowired
    private ProblemRepositoryJPA problemRepositoryJPA;

    @Autowired
    private AlgorithmRepositoryJPA algorithmRepositoryJPA;

    @Autowired
    private ProblemCollectionRepositoryJPA problemCollectionRepositoryJPA;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UUID problemId;
    private UUID algorithm1Id;
    private UUID algorithm2Id;

    @BeforeEach
    void setUp() {
        // Setup in a transaction that will be committed
        transactionTemplate.execute(status -> {
            // Create and save a problem collection
            ProblemCollection problemCollection = new ProblemCollection();
            problemCollection.setId(UUID.randomUUID());
            problemCollection.setName("Test Collection");
            problemCollection.setColor(Color.BLUE);
            problemCollectionRepositoryJPA.save(problemCollection);

            // Create and save a problem
            Problem problem = new ContinuousProblem();
            problem.setId(UUID.randomUUID());
            problem.setName("Test Problem");
            problem.setColor(Color.RED);
            problem.setValueBestKnownSolution(100);
            problem.setNumberRuns(10);
            problem.setVertexSize(1.0);
            problem.setArrowSize(0.5);
            problem.setTreeLayout(false);
            problem.setProblemCollection(problemCollection);
            problemRepositoryJPA.save(problem);

            problemId = problem.getId();

            // Create and save algorithms associated with the problem
            Algorithm algorithm1 = new Algorithm();
            algorithm1.setId(UUID.randomUUID());
            algorithm1.setName("Algorithm 1");
            algorithm1.setColor(Color.GREEN);
            algorithm1.setProblem(problem);
            algorithmRepositoryJPA.save(algorithm1);

            algorithm1Id = algorithm1.getId();

            Algorithm algorithm2 = new Algorithm();
            algorithm2.setId(UUID.randomUUID());
            algorithm2.setName("Algorithm 2");
            algorithm2.setColor(Color.YELLOW);
            algorithm2.setProblem(problem);
            algorithmRepositoryJPA.save(algorithm2);

            algorithm2Id = algorithm2.getId();

            return null;
        });
    }

    @Test
    void execute_shouldCascadeDeleteAlgorithms() {
        // Arrange - Verify initial state in a separate transaction
        Boolean problemExists = transactionTemplate.execute(status -> 
            problemRepositoryJPA.existsById(problemId)
        );
        Boolean algorithm1Exists = transactionTemplate.execute(status -> 
            algorithmRepositoryJPA.existsById(algorithm1Id)
        );
        Boolean algorithm2Exists = transactionTemplate.execute(status -> 
            algorithmRepositoryJPA.existsById(algorithm2Id)
        );

        assertThat(problemExists).isTrue();
        assertThat(algorithm1Exists).isTrue();
        assertThat(algorithm2Exists).isTrue();

        // Act - Delete the problem in a transaction that will be committed
        transactionTemplate.execute(status -> {
            deleteProblemUseCase.execute(problemId);
            return null;
        });

        // Assert - Verify deletion in a separate transaction
        Boolean problemExistsAfter = transactionTemplate.execute(status -> 
            problemRepositoryJPA.existsById(problemId)
        );
        Boolean algorithm1ExistsAfter = transactionTemplate.execute(status -> 
            algorithmRepositoryJPA.existsById(algorithm1Id)
        );
        Boolean algorithm2ExistsAfter = transactionTemplate.execute(status -> 
            algorithmRepositoryJPA.existsById(algorithm2Id)
        );

        assertThat(problemExistsAfter).isFalse();
        assertThat(algorithm1ExistsAfter).isFalse();
        assertThat(algorithm2ExistsAfter).isFalse();
    }
}
