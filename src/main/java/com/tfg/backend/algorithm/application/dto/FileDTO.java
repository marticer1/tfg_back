package com.tfg.backend.algorithm.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private UUID id;
    
    @JsonProperty("name")
    private String fileName;
    
    @JsonProperty("type")
    private String fileType;
    
    private String content;
}
