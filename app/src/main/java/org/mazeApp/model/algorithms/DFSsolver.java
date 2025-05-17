package org.mazeApp.model.algorithms;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Implémentation de l'algorithme de parcours en profondeur (DFS)
 * avec visualisation et mesure du temps d'exécution.
 */
public class DFSsolver {

    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;

    /**
     * Constructeur du solveur DFS
     */
    public DFSsolver(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }

    /**
     * Exécute l'algorithme DFS et anime les étapes dans la vue
     */
    public void visualize() {
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        // ⏱ Début du chronométrage
        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);

        // ⏱ Fin du chronométrage
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
     * Effectue un parcours DFS et retourne toutes les étapes intermédiaires
     */
    private ArrayList<ArrayList<Integer>> solveDFSWithSteps(int start, int end) {
        ArrayList<ArrayList<Integer>> steps = new ArrayList<>();
        boolean[] visited = new boolean[model.getVertexNb()];
        ArrayList<Integer> path = new ArrayList<>();
        dfsRecursive(start, end, visited, path, steps);
        return steps;
    }

    /**
     * DFS récursif avec enregistrement des chemins intermédiaires
     */
    private boolean dfsRecursive(int current, int target, boolean[] visited,
                                 ArrayList<Integer> path, ArrayList<ArrayList<Integer>> steps) {
        visited[current] = true;
        path.add(current);
        steps.add(new ArrayList<>(path)); // Sauvegarder l'étape

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
     * Trouve un chemin sans animation (peut être utilisé pour tests ou algo combinés)
     */
    public ArrayList<Integer> findPath(int start, int end) {
        ArrayList<ArrayList<Integer>> steps = solveDFSWithSteps(start, end);
        if (steps.isEmpty()) return null;
        return steps.get(steps.size() - 1);
    }
}
