package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Abstract base class providing common functionalities for maze solvers.
 * <p>
 * This class implements shared logic such as timing execution and storing 
 * the resulting path. All concrete solver classes should extend this class.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 */
public abstract class AbstractMazeSolver implements MazeSolver {
    protected Graph model;
    protected GraphView graphView;
    protected MazeView mazeView;
    protected List<Integer> finalPath;
    protected long executionTime;
    protected int visitedVerticesNb;
    
    /**
     * Default constructor initializing the path and execution time.
     */
    public AbstractMazeSolver() {
        this.finalPath = new ArrayList<>();
        this.executionTime = 0;
        this.visitedVerticesNb = 0;
    }
    
    /**
     * Sets up the solver with required components: graph model and views.
     *
     * @param graph     the graph representing the maze
     * @param graphView the view for the graph
     * @param mazeView  the view for the maze
     * @return the solver instance itself (for chaining)
     */
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        this.model = graph;
        this.graphView = graphView;
        this.mazeView = mazeView;
        return this;
    }

    /**
     * @returns Returns the execution time of the last solving operation in milliseconds.
     */
    @Override
    public long getExecutionTime() {
        return executionTime;
    }
    
    /**
     * Returns the final path found by the solver.
     *
     * @return a list of vertex indices representing the path
     */
    @Override
    public List<Integer> getFinalPath() {
        return finalPath;
    }
    
    /**
     * @return Returns the number of visited Vertices/Squares in a Graph/Maze 
     * */
    @Override
    public int getvisitedVerticesNumber(){
      return this.visitedVerticesNb;
    }

    /**
     * Utility method to measure the execution time of a given operation.
     * @param operation a Runnable containing the algorithm logic to time
     */
    protected void measureExecutionTime(Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long endTime = System.currentTimeMillis();
        this.executionTime = endTime - startTime;
    }

}
