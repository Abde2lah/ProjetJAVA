package org.mazeApp.model.algorithms;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Cette classe contient l'implémentation de l'algorithme de parcours en profondeur (DFS)
 * pour résoudre un labyrinthe
 */
public class DFSsolver {
    
    /**
     * Classe interne pour représenter une étape du parcours DFS
     */
    public static class DFSStep {
        private int current;
        private ArrayList<Integer> pathSoFar;

        public DFSStep(int current, ArrayList<Integer> path) {
            this.current = current;
            this.pathSoFar = new ArrayList<>(path);
        }
        
        public int getCurrent() {
            return current;
        }
        
        public ArrayList<Integer> getPathSoFar() {
            return pathSoFar;
        }
    }
    
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    
    /**
     * Constructeur
     * 
     * @param model Le graphe à parcourir
     * @param graphView La vue du graphe pour la visualisation
     * @param mazeView La vue du labyrinthe pour la visualisation
     */
    public DFSsolver(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }
    
    /**
     * Exécute l'algorithme DFS avec visualisation animée
     */
    public void visualize() {
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();

        if (start < 0 || end < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        ArrayList<DFSStep> steps = getDFSVisitSteps(start, end);
        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }

        animateSteps(steps);
    }
    
    /**
     * Anime les étapes du parcours DFS
     * 
     * @param steps Les étapes du parcours à animer
     */
    private void animateSteps(ArrayList<DFSStep> steps) {
        mazeView.draw();
        Timeline timeline = new Timeline();
        int delay = 100;

        for (int i = 0; i < steps.size(); i++) {
            final DFSStep step = steps.get(i);
            KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                graphView.highlightVertex(step.getCurrent(), model);
                mazeView.drawPath(step.getPathSoFar());
            });
            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            // Quand l'animation est terminée, dessiner le chemin sur le labyrinthe
            mazeView.draw();
            ArrayList<Integer> finalPath = steps.get(steps.size() - 1).getPathSoFar();
            mazeView.drawPath(finalPath);
            System.out.println("Chemin trouvé de longueur " + finalPath.size());
        });

        timeline.play();
    }


    /**
     * Génère les étapes du parcours DFS
     * 
     * @param start Indice du sommet de départ
     * @param end Indice du sommet d'arrivée
     * @return Liste des étapes du parcours
     */
    public ArrayList<DFSStep> getDFSVisitSteps(int start, int end) {
        ArrayList<DFSStep> steps = new ArrayList<>();
        boolean[] visited = new boolean[model.getVertexNb()];
        ArrayList<Integer> path = new ArrayList<>();
        dfsWithSteps(start, end, visited, path, steps);
        return steps;
    }

    /**
     * DFS récursif avec enregistrement des étapes
     * 
     * @param current Sommet courant
     * @param target Sommet cible
     * @param visited Tableau des sommets visités
     * @param path Chemin parcouru jusqu'à présent
     * @param steps Liste des étapes à enregistrer
     * @return true si un chemin est trouvé, false sinon
     */
    private boolean dfsWithSteps(int current, int target, boolean[] visited,
                               ArrayList<Integer> path, ArrayList<DFSStep> steps) {
        visited[current] = true;
        path.add(current);
        // mazeView.drawPath(path);
        steps.add(new DFSStep(current, path)); // enregistre l'étape

        if (current == target) return true;

        for (Edges edge : model.getGraphMaze().get(current)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor]) {
                if (dfsWithSteps(neighbor, target, visited, path, steps)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1); // backtracking
        return false;
    }
    
    /**
     * Trouve un chemin entre deux points sans visualisation
     * 
     * @param start Indice du sommet de départ
     * @param end Indice du sommet d'arrivée
     * @return Liste des sommets constituant le chemin, ou null si aucun chemin n'existe
     */
    public ArrayList<Integer> findPath(int start, int end) {
        ArrayList<DFSStep> steps = getDFSVisitSteps(start, end);
        if (steps.isEmpty()) {
            return null;
        }
        return steps.get(steps.size() - 1).getPathSoFar();
    }
}