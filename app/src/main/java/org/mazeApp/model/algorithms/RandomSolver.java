package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Random;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

public class RandomSolver {

    private ArrayList<ArrayList<Edges>> graphMaze;
    private Graph graph;
    private MazeView mazeView;
    private int start;
    private int goal;
    private int vertexCount;

    public RandomSolver(Graph graph, MazeView mazeView) {
        this.graph = graph;
        this.vertexCount = graph.getVertexNb();
        this.graphMaze = graph.getGraphMaze();
        this.mazeView = mazeView;
        this.start = mazeView != null ? mazeView.getStartIndex() : -1;
        this.goal = mazeView != null ? mazeView.getEndIndex() : -1;
    }

    /**
    Solve the maze using a random walk algorithm.
    */
    public ArrayList<ArrayList<Integer>> solveRandomWalkSteps() {
        if (mazeView != null) {
            this.start = mazeView.getStartIndex();
            this.goal = mazeView.getEndIndex();
        }

        Random rand = new Random();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        ArrayList<Integer> path = new ArrayList<>();
        path.add(start);
        visited[start] = true;
        allSteps.add(new ArrayList<>(path)); // Add the first step

        while (!path.isEmpty()) {
            int current = path.get(path.size() - 1);

            if (current == goal) {
                return allSteps;
            }

            ArrayList<Integer> unvisitedNeighbours = new ArrayList<>();
            for (Edges edge : graphMaze.get(current)) {
                int neighbor = edge.getDestination();
                if (!visited[neighbor]) {
                    unvisitedNeighbours.add(neighbor);
                }
            }

            if (!unvisitedNeighbours.isEmpty()) {
                int next = unvisitedNeighbours.get(rand.nextInt(unvisitedNeighbours.size()));
                path.add(next);
                visited[next] = true;
                allSteps.add(new ArrayList<>(path));
            } else {
                path.remove(path.size() - 1);
                allSteps.add(new ArrayList<>(path)); 
            }
        }
        return allSteps; // Can be empty if no path found
    }

    /**
     * Visualize the random walk algorithm step by step.
     */
    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualisation non disponible en mode terminal.");
            return;
        }

        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please define a start and end point.");
            return;
        }

        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Algorithm duration of Random Walk : " + duration + " ms");
        //Take the last step
        System.out.println("Path found: " + steps.get(steps.size() - 1));
        mazeView.visualiseStep(steps);
    }

    public RandomSolver(Graph graph, int start, int end) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.mazeView = null;
        this.start = start;
        this.goal = end;
    }
}
