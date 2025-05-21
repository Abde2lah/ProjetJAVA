package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * A data class to encapsulate the result of a maze-solving algorithm.
 * <p>
 * This class stores the final path found by the algorithm, the time it took to compute,
 * and the number of nodes explored during the process.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 */
public class AlgorithmResult {
    private List<Integer> path;
    private long executionTime;
    private int exploredNodes;
    
    /**
     * Constructs a new AlgorithmResult object with the provided data.
     *
     * @param path           the final path found (can be null, defaults to empty list)
     * @param executionTime  the time taken to execute the algorithm (in milliseconds)
     * @param exploredNodes  the number of nodes that were explored during the process
     */
    public AlgorithmResult(List<Integer> path, long executionTime, int exploredNodes) {
        this.path = path != null ? path : new ArrayList<>();
        this.executionTime = executionTime;
        this.exploredNodes = exploredNodes;
    }
    
    /**
     * Returns the final path found by the algorithm.
     *
     * @return a list of node indices representing the path
     */
    public List<Integer> getPath() { return path; }

    /**
     * Returns the execution time of the algorithm.
     *
     * @return the execution time in milliseconds
     */
    public long getExecutionTime() { return executionTime; }

    /**
     * Returns the number of nodes that were explored during the execution.
     *
     * @return the number of explored nodes
     */
    public int getExploredNodes() { return exploredNodes; }

    /**
     * Returns the length of the path.
     *
     * @return the number of nodes in the final path
     */
    public int getPathLength() { return path.size(); }

    /**
     * Checks whether a valid path was found.
     *
     * @return true if a path exists, false otherwise
     */
    public boolean hasPath() { return !path.isEmpty(); }
}