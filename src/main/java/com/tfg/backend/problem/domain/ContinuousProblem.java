package com.tfg.backend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ContinuousProblem")
@Getter
@Setter
@NoArgsConstructor
public class ContinuousProblem extends Problem {
}
