package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Main controller for the maze application.
 * This class handles the generation and clearing of mazes,
 * as well as the interaction with the user interface.
 */
public class MazeController {
    
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    
    // UI Components
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
    private VBox algoButtonContainer;
    
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
     * Initialize UI components.
     */
    private void initializeUIComponents() {
        // Create labels and input fields
        Text rowLabel = new Text("Number of rows:");
        this.rowInput = new TextField("5");  // Default value
        Text colLabel = new Text("Number of columns:");
        this.colInput = new TextField("5");  // Default value
        Text seedLabel = new Text("Seed:");
        this.seedInput = new TextField("42");  // Default value
        
        // Create buttons
        this.clearButton = new Button("Clear");
        this.generateButton = new Button("Generate");

        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.PrimButton = new Button("Prim");
        this.KruskalButton = new Button("Kruskal");
        this.DijkstraButton = new Button("Dijkstra");
        this.RandomButton = new Button("Random");
        this.RightPathButton = new Button("Right Path");
        
        // Create containers
        this.inputContainer = new VBox(10);
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);
        this.algoButtonContainer = new VBox(10);
        
        // Add buttons to the algorithm container
        Label algoLabel = new Label("Algorithm Buttons");
        algoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 5;");
        algoButtonContainer.getChildren().add(algoLabel);
        algoButtonContainer.getChildren().addAll(
            this.DFSButton, this.BFSButton, 
            this.AStarButton, this.PrimButton,
            this.KruskalButton, this.DijkstraButton,
            this.RandomButton, this.RightPathButton
        );
        
        // Add input fields to the input container
        inputContainer.getChildren().addAll(
            rowLabel, this.rowInput, 
            colLabel, this.colInput, 
            seedLabel, this.seedInput
        );

        // Style the algorithm button container
        algoButtonContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    }
    
    /**
     * Set up actions for buttons.
     */
    private void setupButtonActions() {
        // Action for the generate button
        this.generateButton.setOnAction(e -> {
            try {
                int rows = Integer.parseInt(this.rowInput.getText());
                int columns = Integer.parseInt(this.colInput.getText());
                int seed;
                
                if (this.seedInput.getText().isEmpty()) {
                    seed = (int) (Math.random() * Integer.MAX_VALUE);
                    this.seedInput.setText(String.valueOf(seed));
                } else {
                    seed = Integer.parseInt(this.seedInput.getText());
                }
                
                generateMaze(rows, columns, seed);
            } catch (NumberFormatException ex) {
                System.out.println("Error: Please enter valid numbers.");
            }
        });

        // Action for the clear button
        this.clearButton.setOnAction(e -> {
            clearMaze();
        });

        // Action for the DFS button

    }
    
    /**
     * Set up containers for views.
     */
    private void setupContainers() {
        // Create labels for each view
        Label graphLabel = new Label("Graph View");
        Label mazeLabel = new Label("Maze View");
        
        // Style the labels
        graphLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        mazeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Add views to the containers
        this.graphContainer.getChildren().addAll(graphLabel, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeLabel, this.mazeView);
    }
    
    /**
     * Generate a new maze with the given settings.
     */
    public void generateMaze(int rows, int columns, int seed) {
        if (rows < 2 || columns < 2) {
            System.out.println("Error: Dimensions must be at least 2x2.");
            return;
        }
        
        System.out.println("Generating a " + rows + "x" + columns + " maze with seed " + seed);
        
        // Create a new graph with the current settings
        this.model = new Graph(seed, rows, columns);
        refreshViews();
    }
    
    /**
     * Clear the current maze.
     */
    public void clearMaze() {
        System.out.println("Clearing the maze.");
        this.model.clearGraph();
        refreshViews();
    }
    
    /**
     * Refresh the views.
     */
    public void refreshViews() {
        this.graphView.draw(this.model);
        this.mazeView.draw(this.model);
    }
    
    // Getters for model and UI containers
    
    /**
     * Return the current model.
     */
    public Graph getModel() {
        return model;
    }
    
    /**
     * Set a new model and refresh views.
     */
    public void setModel(Graph model) {
        this.model = model;
        refreshViews();
    }
    
    /**
     * Return the input container.
     */
    public VBox getInputContainer() {
        return this.inputContainer;
    }
    
    /**
     * Return the graph view container.
     */
    public VBox getGraphContainer() {
        return this.graphContainer;
    }
    
    /**
     * Return the maze container.
     */
    public VBox getMazeContainer() {
        return this.mazeContainer;
    }
    
    /**
     * Return the algorithm button container.
     */
    public VBox getAlgoButtonContainer() {
        return this.algoButtonContainer;
    }
}