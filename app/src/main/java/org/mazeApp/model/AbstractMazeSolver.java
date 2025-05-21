package org.mazeApp.model;

import java.util.ArrayList;
import java.util.List;

import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * AbstractMazeSolver provides a base implementation for all maze solver algorithms.
 * <p>
 * This abstract class handles shared functionality such as storing the graph model,
 * linked views, result path, and execution time. Subclasses are expected to implement
 * their own pathfinding logic.
 * </p>
 *
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public abstract class AbstractMazeSolver implements MazeSolver {
    protected Graph model;
    protected GraphView graphView;
    protected MazeView mazeView;
    protected List<Integer> finalPath;
    protected long executionTime;
    public AbstractMazeSolver() {
        this.finalPath = new ArrayList<>();
        this.executionTime = 0;
    }
    
    /**
     * Sets up the solver with the graph model, graph view, and maze view.
     *
     * @param graph the graph model representing the maze
     * @param graphView the graphical view of the graph
     * @param mazeView the visual representation of the maze
     * @return the configured MazeSolver instance
     */
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        this.model = graph;
        this.graphView = graphView;
        this.mazeView = mazeView;
        return this;
    }
    
    /**
     * Returns the time taken by the solver to compute the solution.
     *
     * @return the execution time in milliseconds
     */
    @Override
    public long getExecutionTime() {
        return executionTime;
    }
    
    /**
     * Returns the final computed path after the solver completes.
     *
     * @return a list of vertex indices representing the path
     */
    @Override
    public List<Integer> getFinalPath() {
        return finalPath;
    }
    
    /**
     * Measures the execution time of a given operation and stores the result.
     *
     * @param operation the operation (typically the pathfinding logic) to be timed
     */
    protected void measureExecutionTime(Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long endTime = System.currentTimeMillis();
        this.executionTime = endTime - startTime;
    }
 
}
