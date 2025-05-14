package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
    Main controller for the maze application
    This class handles the generation and clearing of mazes,
    as well as the interaction with the user interface.
 */
public class MazeController {
    
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    
    // Composants UI
    private TextField rowInput;
    private TextField colInput;
    private TextField seedInput;
    private Button clearButton;
    private Button generateButton;
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button PrimButton;
    private Button KruskalButton;
    private Button DijkstraButton;
    private Button RandomButton;
    private Button RightPathButton;
    private VBox inputContainer;
    private VBox graphContainer;
    private VBox mazeContainer;
    private VBox AlgobuttonContainer;
    
    public MazeController(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;


        
        // Initialize UI components
        initializeUIComponents();
        setupButtonActions();
        setupContainers();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUIComponents() {
        // Label creation
        Text rowLabel = new Text("Nombre de lignes :");
        this.rowInput = new TextField("5");  // Valeur par défaut
        Text colLabel = new Text("Nombre de colonnes :");
        this.colInput = new TextField("5");  // Valeur par défaut
        Text seedLabel = new Text("Graine :");
        this.seedInput = new TextField("42");  // Valeur par défaut
        
        // Button creation
        this.clearButton = new Button("Effacer");
        this.generateButton = new Button("Générer");

        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.PrimButton = new Button("Prim");
        this.KruskalButton = new Button("Kruskal");
        this.DijkstraButton = new Button("Dijkstra");
        this.RandomButton = new Button("Random");
        this.RightPathButton = new Button("Right Path");
        
        // Container creations
        this.inputContainer = new VBox(10);
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);
        this.AlgobuttonContainer = new VBox(10);
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(this.clearButton, this.generateButton);

        VBox algobuttonContainer = new VBox(10);
        algobuttonContainer.getChildren().addAll(
            this.DFSButton, this.BFSButton, 
            this.AStarButton, this.PrimButton,
            this.KruskalButton, this.DijkstraButton,
            this.RandomButton, this.RightPathButton
        );
        
        // Adding components to the input container
        inputContainer.getChildren().addAll(
            rowLabel, this.rowInput, 
            colLabel, this.colInput, 
            seedLabel, this.seedInput
        );
    }
    

    

    /**
     * setup actions for buttons
     */
    private void setupButtonActions() {
        this.generateButton.setOnAction(e -> {
            try {
                int lignes = Integer.parseInt(this.rowInput.getText());
                int colonnes = Integer.parseInt(this.colInput.getText());
                int seed;
                
                if (this.seedInput.getText().isEmpty()) {
                    seed = (int) (Math.random() * Integer.MAX_VALUE);
                    this.seedInput.setText(String.valueOf(seed));
                } else {
                    seed = Integer.parseInt(this.seedInput.getText());
                }
                
                generateMaze(lignes, colonnes, seed);
            } catch (NumberFormatException ex) {
                System.out.println("Erreur: Veuillez entrer des nombres valides");
            }
        });

        this.clearButton.setOnAction(e -> {
            clearMaze();
        });
    }
    
    /**
     * Setup container for views
     */
    private void setupContainers() {
        // Create label for each views
        Label graphLabel = new Label("Vue du Graphe");
        Label mazeLabel = new Label("Vue du Labyrinthe");
        
        // Styliser les étiquettes
        graphLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        mazeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Add views to the containers
        this.graphContainer.getChildren().addAll(graphLabel, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeLabel, this.mazeView);
    }
    
    /**
     * Génerate new maze with the given settings
     */
    public void generateMaze(int rows, int columns, int seed) {
        if (rows < 2 || columns < 2) {
            System.out.println("Erreur: Les dimensions doivent être d'au moins 2x2");
            return;
        }
        
        System.out.println("Génération d'un labyrinthe " + rows + "x" + columns + " avec la graine " + seed);
        
        // Create new graph with current settings
        this.model = new Graph(seed, rows, columns);
        refreshViews();
    }
    
    /**
     * Erase current graph
     */
    public void clearMaze() {
        System.out.println("Effacement du graphe");
        this.model.clearGraph();
        refreshViews();
    }
    
    /**
     * Refresh views
     */
    public void refreshViews() {
        this.graphView.draw(this.model);
        this.mazeView.draw(this.model);
    }
    
    // Getters for model and UI containers
    
    /**
     * Return the current model
     */
    public Graph getModel() {
        return model;
    }
    
    /**
     * Défine a model
     */
    public void setModel(Graph model) {
        this.model = model;
        refreshViews();
    }
    
    /**
     * Return Input container
     */
    public VBox getInputContainer() {
        return this.inputContainer;
    }
    
    /**
     * Return Graph view container
     */
    public VBox getGraphContainer() {
        return this.graphContainer;
    }
    
    /**
     * Return Maze container
     */
    public VBox getMazeContainer() {
        return this.mazeContainer;
    }
    /**
     * Return Algobutton container
     */
    public VBox getAlgobuttonContainer() {
        return this.AlgobuttonContainer;
    }


}