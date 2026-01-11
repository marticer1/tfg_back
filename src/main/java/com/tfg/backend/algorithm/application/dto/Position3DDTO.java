package com.tfg.backend.algorithm.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position3DDTO {
    private Double x;
    private Double y;
    private Double z;
}
