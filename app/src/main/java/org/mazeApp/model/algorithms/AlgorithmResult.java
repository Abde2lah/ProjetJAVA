package org.mazeApp.model.algorithms;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe pour encapsuler les résultats d'un algorithme
 */
public class AlgorithmResult {
    private List<Integer> path;
    private long executionTime;
    private int exploredNodes;
    
    public AlgorithmResult(List<Integer> path, long executionTime, int exploredNodes) {
        this.path = path != null ? path : new ArrayList<>();
        this.executionTime = executionTime;
        this.exploredNodes = exploredNodes;
    }
    
    // Getters
    public List<Integer> getPath() { return path; }
    public long getExecutionTime() { return executionTime; }
    public int getExploredNodes() { return exploredNodes; }
    public int getPathLength() { return path.size(); }
    public boolean hasPath() { return !path.isEmpty(); }
}
