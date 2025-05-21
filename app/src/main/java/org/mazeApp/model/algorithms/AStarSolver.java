package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Implementation of the A* algorithm for solving a maze
 */
public class AStarSolver extends AbstractMazeSolver {

    /**
     * Constructeur par défaut
     */
    public AStarSolver() {
        super();
    }

    /**
     * Constructeur avec paramètres
     */
    public AStarSolver(Graph model, GraphView graphView, MazeView mazeView) {
        super();
        setup(model, graphView, mazeView);
    }

    /**
     * Launch the A* algorithm with step-by-step visualization
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
            System.out.println("Please define a Start and end point");
            return;
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = getAStarSteps(start, end);
            
            if (steps.isEmpty()) {
                System.out.println("No path found");
                this.finalPath = new ArrayList<>();
            } else {
                this.finalPath = steps.get(steps.size() - 1);
                mazeView.visualiseStep(steps);
            }
        });
        
        System.out.println("A* algorithm duration: " + getExecutionTime() + " ms");
    }


    /**
     * Launch the A* algorithm with non visualization
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
            ArrayList<ArrayList<Integer>> steps = getAStarSteps(start, end);
            
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
    
    @Override
    public List<Integer> findPath(int start, int end) {
        if (model == null) {
            System.out.println("Graph model is null. Cannot find path.");
            return new ArrayList<>();
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = getAStarSteps(start, end);
            if (!steps.isEmpty()) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(finalPath);
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
