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
 * Implementation of Dijkstra's algorithm for maze solving.
 * <p>
 * This class extends {@link AbstractMazeSolver} and implements both animated
 * and non-animated visualization of the shortest path from a start to an end node.
 * It uses a priority queue to ensure optimal exploration of the graph.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 */
public class DijkstraSolver extends AbstractMazeSolver {

    /**
     * Default constructor for factory instantiation.
     */
    public DijkstraSolver() {
        super();
    }

    /**
     * Constructor that sets up the solver with the graph and view components.
     *
     * @param model the maze graph model
     * @param graphView the view for the graph (can be null)
     * @param mazeView the maze visualization view (can be null for headless use)
     */
    public DijkstraSolver(Graph model, GraphView graphView, MazeView mazeView) {
        super();
        setup(model, graphView, mazeView);
    }

    /**
     * Launch the Dijkstra algorithm with step-by-step visualization
     */
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("MazeView is null. Cannot visualize.");
            return;
        }
        
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        // if (start < 0 || end < 0) {
        //     System.out.println("Please define a start and a end point");
        //     return;
        // }

        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = getDijkstraSteps(start, end);

            if (steps.isEmpty()) {
                System.out.println("No path found");
                this.finalPath = new ArrayList<>();
                return;
            }

            this.finalPath = steps.get(steps.size() - 1);
            mazeView.visualiseStep(steps);
        });

        System.out.println("Dijkstra algorithm duration: " + getExecutionTime() + " ms");
    }

    /**
     * Executes Dijkstra's algorithm and visualizes the final result without animation.
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
            ArrayList<ArrayList<Integer>> steps = getDijkstraSteps(start, end);
            
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
     * Executes Dijkstra's algorithm and returns a list of step-by-step paths.
     *
     * @param start the starting vertex
     * @param goal the target vertex
     * @return a list of paths representing each step of the algorithm
     */
    public ArrayList<ArrayList<Integer>> getDijkstraSteps(int start, int goal) {
        int vertexCount = model.getVertexNb();
        ArrayList<ArrayList<Edges>> adj = model.getGraphMaze();

        int[] dist = new int[vertexCount];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        int[] prev = new int[vertexCount];
        Arrays.fill(prev, -1);

        boolean[] visited = new boolean[vertexCount];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        queue.add(new Node(start, 0));

        ArrayList<ArrayList<Integer>> steps = new ArrayList<>();

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int current = currentNode.vertex;

            if (visited[current]) continue;
            visited[current] = true;
            this.visitedVerticesNb++;
            // Save the current path
            ArrayList<Integer> currentPath = reconstructPath(prev, current);
            steps.add(currentPath);

            if (current == goal) break;

            for (Edges edge : adj.get(current)) {
                int neighbor = edge.getDestination();
                int newDist = dist[current] + 1;

                if (newDist < dist[neighbor]) {
                    dist[neighbor] = newDist;
                    prev[neighbor] = current;
                    queue.add(new Node(neighbor, newDist));
                }
            }
        }
        
        ArrayList<Integer> finalPath = reconstructPath(prev, goal);
        System.out.println("Path found : " + finalPath);
        return steps;
    }

    /**
     * Reconstructs the path from the source to the current node using a 'prev' map.
     *
     * @param prev an array of predecessors
     * @param current the destination vertex
     * @return a list of vertex indices forming the path
     */
    private ArrayList<Integer> reconstructPath(int[] prev, int current) {
        ArrayList<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(0, current);
            current = prev[current];
        }
        return path;
    }

    /**
     * Finds the shortest path from start to end using Dijkstra's algorithm.
     * This version does not produce any visual output.
     *
     * @param start the starting node
     * @param end the target node
     * @return a list of vertex indices representing the shortest path
     */
    @Override
    public List<Integer> findPath(int start, int end) {
        if (model == null) {
            System.out.println("Graph model is null. Cannot find path.");
            return new ArrayList<>();
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = getDijkstraSteps(start, end);
            if (!steps.isEmpty()) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(this.finalPath);
    }

    /**
     * Internal utility class to represent a node in the priority queue.
     * Implements comparison by shortest tentative distance.
     */
    private static class Node {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
}
