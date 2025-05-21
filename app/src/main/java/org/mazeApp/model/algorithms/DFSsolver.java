package org.mazeApp.model.algorithms;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Depth-First Search (DFS) implementation for solving mazes.
 * <p>
 * This class extends {@link AbstractMazeSolver} and provides both animated
 * and non-animated execution modes. DFS explores as deep as possible before
 * backtracking, and this implementation collects each step for visualization.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class DFSsolver extends AbstractMazeSolver {

    /**
     * Default constructor for usage with factory or manual setup.
     */
    public DFSsolver() {
        super();
    }

    /**
     * Constructor with initial setup.
     *
     * @param model the graph model representing the maze
     * @param graphView the associated graph view (optional)
     * @param mazeView the associated maze view (required for visualization)
     */
    public DFSsolver(Graph model, GraphView graphView, MazeView mazeView) {
        super();
        setup(model, graphView, mazeView);
    }

    /**
     * Executes DFS with animated visualization in the MazeView.
     * Displays each traversal step as the algorithm proceeds.
     */
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("MazeView is null. Cannot visualize.");
            return;
        }

        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Please define a Start and a end point");
            return;
        }

        // Use measureExecutionTime to calculate the time execution
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);
            
            if (steps.isEmpty()) {
                System.out.println("No path found");
                this.finalPath = new ArrayList<>();
            } else {
                this.finalPath = steps.get(steps.size() - 1);
                mazeView.visualiseStep(steps);
            }
        });

        System.out.println("DFS algorithm duration : " + getExecutionTime() + " ms");
    }

    /**
     * Executes DFS without animation.
     * Computes the steps and renders the final path only.
     */    
    @Override
    public void nonAnimationVisualize() {
        if (mazeView == null) {
            System.out.println("MazeView is null. Cannot visualize.");
            return;
        }
        
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();
        
        if (start < 0 || end < 0) {
            System.out.println("Please define a Start and end point");
            return;
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);
            
            if (steps.isEmpty()) {
                System.out.println("No path found");
                this.finalPath = new ArrayList<>();
            } else {
                this.finalPath = steps.get(steps.size() - 1);
                mazeView.nonAnimationVisualizeStep(steps);
            }
        });
        
        System.out.println("algorithm duration: " + getExecutionTime() + " ms");
    }

    /**
     * Performs DFS traversal and collects each step into a list.
     *
     * @param start the starting vertex index
     * @param end the target vertex index
     * @return a list of traversal steps (each step is a path snapshot)
     */
    private ArrayList<ArrayList<Integer>> solveDFSWithSteps(int start, int end) {
        ArrayList<ArrayList<Integer>> steps = new ArrayList<>();
        boolean[] visited = new boolean[model.getVertexNb()];
        ArrayList<Integer> path = new ArrayList<>();
        dfsRecursive(start, end, visited, path, steps);
        System.out.println("Path found: " + path);
        return steps;
    }

    /**
     * Recursive helper for DFS traversal with backtracking.
     *
     * @param current the current vertex
     * @param target the goal vertex
     * @param visited a boolean array to track visited vertices
     * @param path the current path being explored
     * @param steps the list of recorded steps for visualization
     * @return true if the target is found, false otherwise
     */
    private boolean dfsRecursive(int current, int target, boolean[] visited,
                                 ArrayList<Integer> path, ArrayList<ArrayList<Integer>> steps) {
        visited[current] = true;
        path.add(current);
        steps.add(new ArrayList<>(path)); // Save the step

        if (current == target) {
            return true;
        }

        for (Edges edge : model.getGraphMaze().get(current)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor]) {
                if (dfsRecursive(neighbor, target, visited, path, steps)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1); // backtracking
        steps.add(new ArrayList<>(path)); // Save the backtracking step
        return false;
    }

    /**
     * Finds a path from start to end using DFS, without visualization.
     * The final path is stored and returned.
     *
     * @param start the starting vertex
     * @param end the destination vertex
     * @return the path as a list of vertex indices
     */
    @Override
    public ArrayList<Integer> findPath(int start, int end) {
        if (model == null) {
            System.out.println("Graph model is null. Cannot find path.");
            return new ArrayList<>();
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);
            if (!steps.isEmpty()) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(finalPath);
    }
}
