package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Random;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
        this.start = mazeView.getStartIndex();
        this.goal = mazeView.getEndIndex();
        this.mazeView = mazeView;
    }

    /**
     * Résout le labyrinthe par une marche aléatoire avec trace des étapes pour l'animation.
     */
    public ArrayList<ArrayList<Integer>> solveRandomWalkSteps() {
        Random rand = new Random();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        ArrayList<Integer> path = new ArrayList<>();
        path.add(start);
        visited[start] = true;
        allSteps.add(new ArrayList<>(path)); // Ajouter la première étape

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

        return allSteps; // Peut être vide si aucun chemin
    }

    /**
     * Visualise la marche aléatoire avec animation.
     */
    public void visualize() {
        if (start < 0 || goal < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Durée de l'algorithme Random Walk : " + duration + " ms");
        mazeView.visualiseStep(steps);
    }


}
