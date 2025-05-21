package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Implementation of the DFS algorithm to solve a maze.
 */
public class DFSsolver extends AbstractMazeSolver {

    /**
     * Constructeur par défaut pour la factory
     */
    public DFSsolver() {
        super();
    }

    /**
     * Constructeur avec paramètres
     */
    public DFSsolver(Graph model, GraphView graphView, MazeView mazeView) {
        super();
        setup(model, graphView, mazeView);
    }

    /**
     * Launches the maze solving with step-by-step visualization
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

        // Utiliser measureExecutionTime pour calculer le temps d'exécution
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
     * Do a DFS search and return the steps taken
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
     * Recursive DFS function
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
     * Find the path from start to end without visualizing the steps
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
