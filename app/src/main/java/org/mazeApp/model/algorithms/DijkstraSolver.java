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
 * Implémentation de l'algorithme de Dijkstra pour résoudre un labyrinthe
 */
public class DijkstraSolver {

    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;

    public DijkstraSolver(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }

    /**
     * Lance la résolution avec visualisation étape par étape
     */
    public void visualize() {
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> steps = getDijkstraSteps(start, end);

        long endTime = System.currentTimeMillis();   
        long duration = endTime - startTime;         

        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }

        mazeView.visualiseStep(steps);
        System.out.println("Durée de l'algorithme Dijkstra : " + duration + " ms");
    }

    /**
     * Retourne les étapes intermédiaires explorées par Dijkstra
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

            // Enregistrer chemin partiel pour animation
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

        return steps;
    }

    /**
     * Reconstitue un chemin depuis le tableau des précédents
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
     * Classe représentant un nœud avec sa distance minimale
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

