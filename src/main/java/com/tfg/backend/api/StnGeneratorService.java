package com.tfg.backend.api;

import com.tfg.backend.problem.application.dto.RegistrationProblemDTO;
import com.tfg.backend.algorithm.application.dto.RegistrationAlgorithmDTO;
import com.tfg.backend.algorithm.application.dto.FileDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class StnGeneratorService {

    private final String pythonExecutable;
    private final Path scriptsDir;

    public StnGeneratorService(
            @Value("${stn.python:python3}") String pythonExecutable,
            @Value("${stn.scriptsDir:python}") String scriptsDir
    ) {
        this.pythonExecutable = pythonExecutable;
        this.scriptsDir = Paths.get(scriptsDir);
    }

    public Path[] generateFromRegistration(RegistrationProblemDTO dto, UUID problemId) throws IOException, InterruptedException {
        // Si no hay algoritmos o ficheros, no podemos generar visualización
        if (dto.getAlgorithms() == null || dto.getAlgorithms().isEmpty()) {
            return null;
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(pythonExecutable);
        cmd.add("stn_cli.py");

        String typeProblem = "discrete";
        if ("ContinuousProblem".equalsIgnoreCase(dto.getProblemType())) {
            typeProblem = "continuous";
        }
        cmd.add("--typeproblem"); cmd.add(typeProblem);

        // Strategy
        if ("discrete".equals(typeProblem)) {
            cmd.add("--strategy"); cmd.add("standard");
        } else {
            cmd.add("--strategy"); cmd.add("agglomerative");
        }

        // Parámetros básicos
        cmd.add("--bmin"); cmd.add("1"); // valor por defecto usado en el Python
        cmd.add("--best"); cmd.add(String.valueOf(dto.getValueBestKnownSolution()));
        cmd.add("--nruns"); cmd.add(String.valueOf(dto.getNumberRuns()));
        cmd.add("--nodesize"); cmd.add(String.valueOf(dto.getVertexSize()));
        cmd.add("--arrowsize"); cmd.add(String.valueOf(dto.getArrowSize()));
        cmd.add("--treelayout"); cmd.add(Boolean.toString(Boolean.TRUE.equals(dto.getTreeLayout())));
        cmd.add("--hash-file"); cmd.add(problemId.toString());

        // Standard (discrete)
        if ("discrete".equals(typeProblem) && dto.getStandardPartitioning() != null) {
            var s = dto.getStandardPartitioning();
            // En stn.py partition_factor se usa como exponente de 10**pf; aquí pasamos el hypercube directamente
            cmd.add("--partition-factor"); cmd.add(String.valueOf(s.getHypercube()));
            cmd.add("--min-bound"); cmd.add(String.valueOf(s.getMinBound()));
            cmd.add("--max-bound"); cmd.add(String.valueOf(s.getMaxBound()));
            // zvalue/partition-value no está en el DTO => usamos 0.0 por defecto
            cmd.add("--partition-value"); cmd.add("0.0");
        }

        // Agglomerative (continuous)
        if ("continuous".equals(typeProblem) && dto.getAgglomerativeClustering() != null) {
            var a = dto.getAgglomerativeClustering();
            cmd.add("--cluster-size"); cmd.add(String.valueOf(a.getClusterSize()));
            cmd.add("--volume-size"); cmd.add(String.valueOf(a.getVolumeSize()));
            //TODO Añadir NumberOfClusters
            /*if (a.getNumberOfClusters() != null) {
                cmd.add("--num-clusters"); cmd.add(String.valueOf(a.getNumberOfClusters()));
            }*/
            if (a.getDistance() != null) {
                cmd.add("--distance-method"); cmd.add(a.getDistance().name().toLowerCase());
            }
        }

        // Create temp directory for temporary files
        Path tempDir = Files.createTempDirectory("stn_files_");
        List<Path> tempFiles = new ArrayList<>();

        try {
            // Ficheros y colores por algoritmo
            for (RegistrationAlgorithmDTO alg : dto.getAlgorithms()) {
                FileDTO f = alg.getFile();
                if (f == null || f.getContent() == null) continue; // saltamos sin contenido
                
                // Decode base64 content and write to temporary file
                byte[] decodedContent = Base64.getDecoder().decode(f.getContent());
                String fileName = f.getFileName() != null ? f.getFileName() : "algorithm_" + UUID.randomUUID() + ".txt";
                Path tempFile = tempDir.resolve(fileName);
                Files.write(tempFile, decodedContent);
                tempFiles.add(tempFile);
                
                String path = tempFile.toString();
                String name = alg.getName() != null ? alg.getName() : "alg";
                String colorHex = "#000000";
                if (alg.getColor() != null) {
                    Color c = alg.getColor();
                    colorHex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                }
                // formato: path:name:#RRGGBB
                cmd.add("--file");
                cmd.add(path.replace('\\','/') + "|" + name + "|" + colorHex);
            }

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(scriptsDir.toFile());
            pb.redirectErrorStream(true);

            Process p = pb.start();
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    outputLines.add(line);
                }
            }
            int code = p.waitFor();
            if (code != 0) {
                throw new IOException("stn_cli.py failed with exit code " + code);
            }
            
            // The script outputs two lines: PDF path and JSON path
            if (outputLines.size() < 2) {
                return null;
            }
            
            String pdfLine = outputLines.get(outputLines.size() - 2).trim();
            String jsonLine = outputLines.get(outputLines.size() - 1).trim();
            
            if (pdfLine.isEmpty() || jsonLine.isEmpty()) {
                return null;
            }

            // Return both paths
            Path pdfPath = scriptsDir.resolve(pdfLine).normalize();
            Path jsonPath = scriptsDir.resolve(jsonLine).normalize();
            return new Path[]{pdfPath, jsonPath};
        } finally {
            // Clean up temporary files
            for (Path tempFile : tempFiles) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    // Log but don't fail if cleanup fails
                }
            }
            try {
                Files.deleteIfExists(tempDir);
            } catch (IOException e) {
                // Log but don't fail if cleanup fails
            }
        }
    }
}
