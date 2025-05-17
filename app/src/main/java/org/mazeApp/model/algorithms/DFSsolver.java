package org.mazeApp.model.algorithms;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
    Implementation of the DFS algorithm to solve a maze.
 */
public class DFSsolver {

    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;

    /**
     * DFS solver constructor
     */
    public DFSsolver(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }

    /**
     * Launches the maze solving with step-by-step visualization
     */
    public void visualize() {
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        // time begin
        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);

        // Time end
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }

        System.out.println("Durée de l'algorithme DFS : " + duration + " ms");
        mazeView.visualiseStep(steps);
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
        return false;
    }

    /**
     * Find the path from start to end without visualizing the steps
     */
    public ArrayList<Integer> findPath(int start, int end) {
        ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);
        if (steps.isEmpty()) return null;
        return steps.get(steps.size() - 1);
    }
}
