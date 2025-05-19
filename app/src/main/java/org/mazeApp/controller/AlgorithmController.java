package org.mazeApp.controller;

import java.util.ArrayList;
import java.util.List;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
import org.mazeApp.model.algorithms.AStarSolver;
import org.mazeApp.model.algorithms.BFSsolver;
import org.mazeApp.model.algorithms.DFSsolver;
import org.mazeApp.model.algorithms.DijkstraSolver;
import org.mazeApp.model.algorithms.OnlyLeftSolver;
import org.mazeApp.model.algorithms.OnlyRightSolver;
import org.mazeApp.model.algorithms.RandomSolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.paint.Color;

/**
 * Controller for the algorithms
 * This class is responsible for managing the algorithm buttons and their actions
 */
public class AlgorithmController {
    
    // Référence au contrôleur principal
    private MainControlleur mainController;
    
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
    private Button PauseButton;         // Bouton pour mettre en pause/reprendre l'animation
    private Label SpeedAnimationLabel;
    private Label TimeExecutionLabel;   // Label pour le temps d'exécution
    private Label PathLengthLabel;      // Label pour la longueur du chemin
    private Slider SpeedAnimationCursor;
    private int delay = 50;
    private VBox AlgoContainer;
    private boolean animationPaused = false;  // État de pause de l'animation

    /**
     * Constructor which initializes the algorithm controller
     */
    public AlgorithmController(Graph graph, MainControlleur mainController) {
        this.mainController = mainController;
        initializeAlgorithmButtons();
        setupAlgorithmButtonActions();
        setupAllButtons();
    }
    
    /**
     * Initialize the algorithm buttons
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
        this.PauseButton = new Button("⏸️ Pause");  // Nouveau bouton pour pause/reprise
        this.TimeExecutionLabel = new Label("Temps : 0 ms");
        this.PathLengthLabel = new Label("Longueur : 0 cases");
        this.SpeedAnimationLabel = new Label("Speed : "+delay+" ms");
        this.SpeedAnimationCursor= new Slider(0, 100, 1);
        
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
        this.PauseButton.setPrefSize(100, 30);
        this.SpeedAnimationCursor.setPrefSize(100, 30);
        
        //styles for the animation label: 
        this.SpeedAnimationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        this.TimeExecutionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        this.PathLengthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        
        // Style pour le bouton pause
        this.PauseButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
        
        // Create a VBox to hold the buttons
        this.AlgoContainer = new VBox(10);
        this.AlgoContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
        
        // Add a title to the algorithm container
        javafx.scene.text.Text algoTitle = new javafx.scene.text.Text("Algorithmes");
        algoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        this.AlgoContainer.getChildren().add(algoTitle);
        
        this.AlgoContainer.getChildren().addAll(
            this.DFSButton,
            this.BFSButton,
            this.AStarButton,
            this.DijkstraButton,
            this.PrimButton,
            this.KruskalButton,
            this.RightButton,
            this.LeftButton,
            this.RandomButton,
            this.PauseButton,         // Ajout du bouton pause
            this.TimeExecutionLabel,  // Ajout du label pour le temps d'exécution
            this.PathLengthLabel,     // Ajout du label pour la longueur du chemin
            this.SpeedAnimationLabel,
            this.SpeedAnimationCursor
        );
    }
    
    /**
     * Set up the actions for the algorithm buttons
     */
    private void setupAlgorithmButtonActions() {
        // Bouton pour mettre en pause/reprendre l'animation
        this.PauseButton.setOnAction(e -> {
            MazeView mazeView = mainController.getMazeView();
            if (mazeView != null) {
                animationPaused = !animationPaused;
                mazeView.setAnimationPaused(animationPaused);
                
                if (animationPaused) {
                    PauseButton.setText("▶️ Reprendre");
                } else {
                    PauseButton.setText("⏸️ Pause");
                }
            }
        });
        
        this.PrimButton.setOnAction(e -> System.out.println("Prim non implémenté"));
        this.KruskalButton.setOnAction(e -> System.out.println("Kruskal non implémenté"));
        
        this.SpeedAnimationCursor.setOnMouseDragged(e -> {
            int delay = (int) SpeedAnimationCursor.getValue();
            SpeedAnimationLabel.setText("Speed : " + delay + " ms");
            mainController.getMazeView().setDelayResolverAnimation(delay);
        });
    }
    
    /**
     * Met en place un timer pour vérifier la fin de l'animation et mettre à jour la longueur du chemin
     */
    private void setupAnimationListener() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            MazeView mazeView = mainController.getMazeView();
            
            // Si l'animation est terminée ou si la MazeView n'est pas en train d'animer
            if (mazeView != null && !mazeView.isAnimationRunning()) {
                // Tenter de trouver le chemin pour mettre à jour la longueur
                Graph model = mainController.getModel();
                int start = mazeView.getStartIndex();
                int end = mazeView.getEndIndex();
                
                // Utiliser DFS qui est plus simple pour trouver le chemin final
                DFSsolver dfsSolver = new DFSsolver(model, null, null);
                List<Integer> path = dfsSolver.findPath(start, end);
                updatePathLengthLabel(path);
                
                // Arrêter le timer
                ((Timeline)event.getSource()).stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Met à jour le label de temps d'exécution
     * @param durationMs Durée en millisecondes
     */
    private void updateTimeExecutionLabel(long durationMs) {
        TimeExecutionLabel.setText("Temps : " + durationMs + " ms");
    }
    
    /**
     * Met à jour le label de longueur du chemin
     * @param path Chemin trouvé
     */
    private void updatePathLengthLabel(List<Integer> path) {
        int length = (path != null) ? path.size() : 0;
        PathLengthLabel.setText("Longueur : " + length + " cases");
    }
    
    /**
     * Crée et configure un solveur du type spécifié
     * @param solverType Type de solveur à créer
     * @return Solveur configuré
     */
    private MazeSolver createSolver(String solverType) {
        Graph model = mainController.getModel();
        GraphView graphView = mainController.getGraphView();
        MazeView mazeView = mainController.getMazeView();
        
        MazeSolver solver;
        
        switch (solverType) {
            case "DFS":
                solver = new DFSsolver();
                break;
            case "BFS":
                solver = new BFSsolver(model.getVertexNb());
                break;
            case "AStar":
                solver = new AStarSolver();
                break;
            case "Dijkstra":
                solver = new DijkstraSolver();
                break;
            case "Random":
                solver = new RandomSolver();
                break;
            case "Right":
                solver = new OnlyRightSolver();
                break;
            case "Left":
                solver = new OnlyLeftSolver();
                break;
            default:
                throw new IllegalArgumentException("Type de solveur inconnu: " + solverType);
        }
        
        return solver.setup(model, graphView, mazeView);
    }

    // Template commun pour tous les boutons d'algorithme
    private void setupAlgorithmButton(Button button, String solverType) {
        button.setOnAction(e -> {
            try {
                MazeSolver solver = createSolver(solverType);
                
                // Exécuter l'algorithme et mesurer le temps
                long startTime = System.currentTimeMillis();
                
                // Trouver le chemin d'abord (pour avoir la longueur)
                MazeView mazeView = mainController.getMazeView();
                List<Integer> path = solver.findPath(mazeView.getStartIndex(), mazeView.getEndIndex());
                updatePathLengthLabel(path);
                
                // Lancer la visualisation
                solver.visualize();
                
                // Mettre à jour le temps d'exécution
                long endTime = System.currentTimeMillis();
                updateTimeExecutionLabel(endTime - startTime);
                
                // Surveillance de fin d'animation si nécessaire
                setupAnimationListener();
            } catch (Exception ex) {
                System.err.println("Erreur lors de l'exécution de " + solverType + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Initialisation de tous les boutons d'algorithme
    private void setupAllButtons() {
        setupAlgorithmButton(DFSButton, "DFS");
        setupAlgorithmButton(BFSButton, "BFS");
        setupAlgorithmButton(AStarButton, "AStar");
        setupAlgorithmButton(DijkstraButton, "Dijkstra");
        setupAlgorithmButton(RandomButton, "Random");
        setupAlgorithmButton(RightButton, "Right");
        setupAlgorithmButton(LeftButton, "Left");
    }
    
    /**
     * Get the algorithm buttons container
     */
    public VBox getAlgoContainer() {
        return this.AlgoContainer;
    }
}