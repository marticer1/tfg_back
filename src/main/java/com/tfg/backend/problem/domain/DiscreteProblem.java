package com.tfg.backend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DiscreteProblem")
@Getter
@Setter
@NoArgsConstructor
public class DiscreteProblem extends Problem {
}
