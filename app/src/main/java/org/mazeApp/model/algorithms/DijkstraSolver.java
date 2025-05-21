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
 * Implémentation of Dijkstra's algorithm to solve a maze.
 */
public class DijkstraSolver extends AbstractMazeSolver {

    // Constructeur par défaut pour la factory
    public DijkstraSolver() {
        super();
    }

    // Constructeur avec paramètres
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
     * Return the steps taken by the Dijkstra algorithm
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
     * Reconstitute the path from the start to the goal using the previous nodes
     */
    private ArrayList<Integer> reconstructPath(int[] prev, int current) {
        ArrayList<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(0, current);
            current = prev[current];
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
     * Class representing a node in the priority queue
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
