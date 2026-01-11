package com.tfg.backend.visualization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * Represents 3D visualization data from R/Python scripts
 */
@Data
public class VisualizationData {
    private List<String> algorithms;
    private List<String> algorithmColors;
    private List<NodeData> nodes;
    private List<EdgeData> edges;
    private Stats stats;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NodeData {
        private String id;
        private String type;
        private Double size;
        private String color;
        // shape removed - frontend will decide based on type
        private Double fitness;
        private String algorithm;
        private Double x_fr;
        private Double y_fr;
        private Double x_kk;
        private Double y_kk;
    }
    
    @Data
    public static class EdgeData {
        private String from;
        private String to;
        private String color;
        private Double width;
    }
    
    @Data
    public static class Stats {
        private Integer node_count;
        private Integer edge_count;
        private Integer component_count;
    }
}
