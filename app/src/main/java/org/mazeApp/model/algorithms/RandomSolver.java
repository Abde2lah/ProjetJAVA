package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

public class RandomSolver extends AbstractMazeSolver {

    private int start = -1;
    private int end = -1;

    // Constructeur par défaut pour la factory
    public RandomSolver() {
        super();
    }

    // Constructeur avec paramètres
    public RandomSolver(Graph graph, MazeView mazeView) {
        super();
        setup(graph, null, mazeView);
    }

    // Constructeur pour terminal
    public RandomSolver(Graph graph, int start, int end) {
        super();
        setup(graph, null, null);
        this.start = start;
        this.end = end;
    }

    /**
     * Solve the maze using a random walk algorithm.
     */
    public ArrayList<ArrayList<Integer>> solveRandomWalkSteps() {
        // Utiliser les valeurs de MazeView si disponibles
        int startIdx = (mazeView != null) ? mazeView.getStartIndex() : this.start;
        int goalIdx = (mazeView != null) ? mazeView.getEndIndex() : this.end;
        
        int vertexCount = model.getVertexNb();
        ArrayList<ArrayList<Edges>> graphMaze = model.getGraphMaze();

        Random rand = new Random();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        ArrayList<Integer> path = new ArrayList<>();
        path.add(startIdx);
        visited[startIdx] = true;
        allSteps.add(new ArrayList<>(path)); // Add the first step

        while (!path.isEmpty()) {
            int current = path.get(path.size() - 1);

            if (current == goalIdx) {
                this.finalPath = new ArrayList<>(path);
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
        
        // Aucun chemin trouvé
        this.finalPath = new ArrayList<>();
        return allSteps;
    }

    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualisation non disponible en mode terminal.");
            return;
        }

        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please define a start and end point.");
            return;
        }

        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
            mazeView.visualiseStep(steps);
        });
        
        System.out.println("Algorithm duration of Random Walk : " + getExecutionTime() + " ms");
        System.out.println("Path found: " + getFinalPath());
    }

    @Override
    public List<Integer> findPath(int start, int end) {
        this.start = start;
        this.end = end;
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
            if (!steps.isEmpty() && steps.get(steps.size() - 1).contains(end)) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(this.finalPath);
    }
}