package org.mazeApp.controller;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.algorithms.DFSsolver;
import org.mazeApp.model.algorithms.RandomSolver;
import org.mazeApp.model.algorithms.DFSsolver.DFSStep;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Contrôleur dédié aux algorithmes de parcours et génération de labyrinthe
 * Hérite de MazeController pour avoir accès aux modèles et vues
 */
public class AlgorithmController extends MainControlleur {
    
    // Boutons spécifiques aux algorithmes
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button DijkstraButton;
    private Button PrimButton;
    private Button KruskalButton;
    private Button RightButton;
    private Button LeftButton;
    private Button RandomButton;
    
    /**
     * Constructeur qui appelle le constructeur parent
     */
    public AlgorithmController(Graph graph) {
        super(graph);
        initializeAlgorithmButtons();
        setupAlgorithmButtonActions();
    }
    
    /**
     * Initialise les boutons pour les algorithmes
     */
    private void initializeAlgorithmButtons() {
        // Create algorithm buttons
        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.DijkstraButton = new Button("Dijkstra");
        this.PrimButton = new Button("Prim");
        this.KruskalButton = new Button("Kruskal");
        this.RightButton = new Button("Right");
        this.LeftButton = new Button("Left");
        this.RandomButton = new Button("Random");
        
        // Give the same size to algo buttons
        this.DFSButton.setPrefSize(100, 30);
        this.BFSButton.setPrefSize(100, 30);
        this.AStarButton.setPrefSize(100, 30);
        this.DijkstraButton.setPrefSize(100, 30);
        this.PrimButton.setPrefSize(100, 30);
        this.KruskalButton.setPrefSize(100, 30);
        this.RightButton.setPrefSize(100, 30);
        this.LeftButton.setPrefSize(100, 30);
        this.RandomButton.setPrefSize(100, 30);
        
        // Add buttons to the algo container from parent
        getAlgoButtonContainer().getChildren().addAll(
            this.DFSButton,
            this.BFSButton,
            this.AStarButton,
            this.DijkstraButton,
            this.PrimButton,
            this.KruskalButton,
            this.RightButton,
            this.LeftButton,
            this.RandomButton
        );
    }
    
    /**
     * Configure les actions des boutons d'algorithmes
     */
    private void setupAlgorithmButtonActions() {
        // Action to solve the maze with DFS
        this.DFSButton.setOnAction(e -> executeDFSAlgorithm());

        // Action to solve the maze with Random
        this.RandomButton.setOnAction(e -> executeRandomAlgorithm());
        
        
        // Autres actions à implémenter
        this.BFSButton.setOnAction(e -> System.out.println("BFS non implémenté"));
        this.AStarButton.setOnAction(e -> System.out.println("A* non implémenté"));
        this.DijkstraButton.setOnAction(e -> System.out.println("Dijkstra non implémenté"));
        this.PrimButton.setOnAction(e -> System.out.println("Prim non implémenté"));
        this.KruskalButton.setOnAction(e -> System.out.println("Kruskal non implémenté"));
        this.RightButton.setOnAction(e -> System.out.println("Right non implémenté"));
        this.LeftButton.setOnAction(e -> System.out.println("Left non implémenté"));
    }
    
    /**
     * Exécute l'algorithme DFS avec visualisation
     */
    private void executeDFSAlgorithm() {
        GraphView graphView = getGraphView();
        MazeView mazeView = getMazeView();
        Graph model = getModel();
        DFSsolver dfsSolver = new DFSsolver(model, graphView, mazeView);
        dfsSolver.visualize();
    }

    private void executeRandomAlgorithm() {
        Graph model = getModel();
        MazeView mazeView = getMazeView();
        RandomSolver randomSolver = new RandomSolver(model, mazeView);
        randomSolver.visualize();
    }
    
    /**
     * Implémentation de l'animation de génération du labyrinthe
     */
    public void animateMazeGeneration() {
        try {
            int rows = getRowValue();
            int columns = getColumnValue();
            int seed = getSeedValue();

            System.out.println("Animation du labyrinthe " + rows + "x" + columns + " avec seed " + seed);

            // Créer un graphe vide
            Graph animatedGraph = Graph.emptyGraph(rows, columns);
            
            // Récupérer les étapes de génération en fonction de l'algorithme actuel
            ArrayList<Edges> steps = Graph.getCurrentGenerator().generate(rows, columns, seed);
            
            MazeView animatedMazeView = new MazeView(animatedGraph, getGraphView());

            setModel(animatedGraph);
            setMazeView(animatedMazeView);
            updateMazeViewInContainer(animatedMazeView);

            Timeline timeline = new Timeline();
            int delay = 50;

            for (int i = 0; i < steps.size(); i++) {
                final int index = i;
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                    Edges edge = steps.get(index);
                    animatedGraph.addEdge(edge.getSource(), edge.getDestination());
                    animatedMazeView.draw();
                });
                timeline.getKeyFrames().add(frame);
            }

            timeline.play();
        } catch (NumberFormatException e) {
            System.out.println("Erreur : entrez des valeurs valides pour lignes/colonnes/seed.");
        }
    }
}