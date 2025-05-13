package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Contrôleur principal pour l'application de labyrinthe
 * Gère les interactions entre le modèle (Graph) et les vues
 */
public class MazeController {
    
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    
    public MazeController(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
    }
    
    /**
     * Génère un nouveau labyrinthe avec les paramètres spécifiés
     */
    public void generateMaze(int rows, int columns, int seed) {
        if (rows < 2 || columns < 2) {
            System.out.println("Erreur: Les dimensions doivent être d'au moins 2x2");
            return;
        }
        
        System.out.println("Génération d'un labyrinthe " + rows + "x" + columns + " avec la graine " + seed);
        
        // Créer le nouveau graphe avec les paramètres fournis
        this.model = new Graph(seed, rows, columns);
        refreshViews();
    }
    
    /**
     * Efface le graphe actuel
     */
    public void clearMaze() {
        System.out.println("Effacement du graphe");
        this.model.clearGraph();
        refreshViews();
    }
    
    /**
     * Rafraîchit les vues pour refléter l'état actuel du modèle
     */
    public void refreshViews() {
        this.graphView.draw(this.model);
        this.mazeView.draw(this.model);
    }
    
    // Getter pour le modèle
    public Graph getModel() {
        return model;
    }
    
    // Setter pour le modèle
    public void setModel(Graph model) {
        this.model = model;
        refreshViews();
    }
}