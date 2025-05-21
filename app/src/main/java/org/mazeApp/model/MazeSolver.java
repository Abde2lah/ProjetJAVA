package org.mazeApp.model;

import java.util.List;

import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Common interface for all maze solving algorithms.
 * <p>
 * This interface defines the required methods that any solver
 * must implement, including basic execution, visualization,
 * and setup.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public interface MazeSolver {
    /**
     * Executes the maze solving algorithm and returns the resulting path.
     *
     * @param start the starting vertex index
     * @param end the ending vertex index
     * @return a list of vertex indices representing the path, or an empty list if no path was found
     */
    List<Integer> findPath(int start, int end);
    
    /**
     * Executes the algorithm and displays the solving process in real-time.
     * This is typically used for animations or visual feedback.
     */
    void visualize();

    /**
     * Executes the algorithm without animation or visual feedback.
     * This is used for silent execution and performance measurement.
     */
    void nonAnimationVisualize();
    
    /**
     * Returns the time it took to execute the algorithm.
     *
     * @return execution time in milliseconds
     */
    long getExecutionTime();
    
    /**
     * Returns the final path that was found by the solver.
     *
     * @return a list of vertex indices representing the final path
     */ 
    List<Integer> getFinalPath();
    
    /**
     * Sets up the solver with the required graph and views for execution and visualization.
     *
     * @param graph the graph structure representing the maze
     * @param graphView the view used for rendering the graph
     * @param mazeView the view used for rendering the maze
     * @return the initialized MazeSolver (this)
     */
    MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView);
}