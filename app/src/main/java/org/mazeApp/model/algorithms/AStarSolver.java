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
 * Implementation of the A* algorithm for solving mazes.
 * <p>
 * This class extends AbstractMazeSolver and uses Manhattan distance
 * as a heuristic to efficiently find the shortest path from start to end.
 * </p>
 * @author Abdellah, Felipe, Jeremy, SHawrov, Melina
 */

public class AStarSolver extends AbstractMazeSolver {

    /**
     * Default constructor.
     */
    public AStarSolver() {
        super();
    }

    /**
     * Constructs an AStarSolver with the provided graph, graph view, and maze view.
     *
     * @param model the graph representing the maze
     * @param graphView the graphical view of the graph (for visualization)
     * @param mazeView the graphical view of the maze (for visualization)
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
     * Runs the A* algorithm from start to goal and returns the steps taken.
     *
     * @param start the index of the starting node
     * @param goal the index of the goal node
     * @return a list of steps, each step being a partial path constructed so far
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
     * Heuristic function used by A* (Manhattan distance).
     *
     * @param a index of the first node
     * @param b index of the second node
     * @return the heuristic distance between a and b
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
     * Reconstructs the shortest path using the cameFrom array.
     *
     * @param cameFrom array mapping each node to its predecessor
     * @param current the node from which to reconstruct the path
     * @return the complete path from start to current
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
     * Finds the shortest path between start and end using the A* algorithm.
     *
     * @param start the starting node
     * @param end the ending node
     * @return a list of node indices representing the shortest path
     */
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
