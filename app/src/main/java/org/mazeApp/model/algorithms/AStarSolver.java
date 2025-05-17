package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Implementation of the A* algorithm for solving a maze
 */
public class AStarSolver {

    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;

    /**
     * Constructor for AStarSolver
     */
    public AStarSolver(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }

    /**
     * Launch the A* algorithm with step-by-step visualization
     */
    public void visualize() {
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> steps = getAStarSteps(start, end);

        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Durée de l'algorithme A* : " + duration + " ms");

        mazeView.visualiseStep(steps);
    }

    /**
     * Return the steps taken by the A* algorithm
     */
    public ArrayList<ArrayList<Integer>> getAStarSteps(int start, int goal) {
        int vertexCount = model.getVertexNb();
        ArrayList<ArrayList<Edges>> adj = model.getGraphMaze();

        // Initialization
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        boolean[] visited = new boolean[vertexCount];
        int[] cameFrom = new int[vertexCount];
        Arrays.fill(cameFrom, -1);

        int[] gScore = new int[vertexCount];
        Arrays.fill(gScore, Integer.MAX_VALUE);
        gScore[start] = 0;

        int[] fScore = new int[vertexCount];
        Arrays.fill(fScore, Integer.MAX_VALUE);
        fScore[start] = heuristic(start, goal);
        openSet.add(new Node(start, fScore[start]));

        ArrayList<ArrayList<Integer>> steps = new ArrayList<>();

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            int current = currentNode.vertex;

            if (visited[current]) continue;
            visited[current] = true;

            // Reconstitute the path
            ArrayList<Integer> currentPath = reconstructPath(cameFrom, current);
            steps.add(currentPath);

            if (current == goal) break;

            for (Edges edge : adj.get(current)) {
                int neighbor = edge.getDestination();
                int tentativeG = gScore[current] + 1;

                if (tentativeG < gScore[neighbor]) {
                    cameFrom[neighbor] = current;
                    gScore[neighbor] = tentativeG;
                    fScore[neighbor] = gScore[neighbor] + heuristic(neighbor, goal);
                    openSet.add(new Node(neighbor, fScore[neighbor]));
                }
            }
        }
        System.out.println("Path found : " + reconstructPath(cameFrom, goal));

        return steps;
    }

    /**
     * Heuristic function for A* algorithm based on Manhattan distance
     */
    private int heuristic(int a, int b) {
        int columns = model.getColumns();
        int ax = a % columns;
        int ay = a / columns;
        int bx = b % columns;
        int by = b / columns;
        return Math.abs(ax - bx) + Math.abs(ay - by);
    }

    /**
     * Reconstitute a path from the cameFrom array
     */
    private ArrayList<Integer> reconstructPath(int[] cameFrom, int current) {
        ArrayList<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(0, current);
            current = cameFrom[current];
        }
        return path;
    }

    /**
     * Internal class representing a node in the priority queue
     */
    private static class Node {
        int vertex;
        int fScore;

        Node(int vertex, int fScore) {
            this.vertex = vertex;
            this.fScore = fScore;
        }
    }
}
