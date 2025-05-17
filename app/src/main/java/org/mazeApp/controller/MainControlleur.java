package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.model.generator.DFSGenerator;
import org.mazeApp.model.generator.KruskalGenerator;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;
import org.mazeApp.view.SaveView;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * Main controller for the maze application.
 * This class handles the generation, clearing, saving, and loading of mazes,
 * as well as the interaction with the user interface.
 */
public class MainControlleur {

    // Modèle et vues
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    private SaveManager saveManager;
    private SaveView saveView;

    // UI Components
    private TextField rowInput;
    private TextField colInput;
    private TextField seedInput;
    private Button clearButton;
    private Button generateButton;
    private Button saveButton;
    private Button loadButton;
    private Button animateGenerationButton;
    private Button toggleGraphButton;
    private VBox inputContainer;
    private VBox graphContainer;
    private VBox mazeContainer;
    private VBox algoButtonContainer;

    // Add a boolean to check if the graph is visible
    private boolean graphVisible = true;

    public MainControlleur(Graph graph) {
        this.model = graph;
        this.graphView = new GraphView();
        this.mazeView = new MazeView(model, graphView);
        this.graphView.draw(model);
        this.mazeView.draw();

        // Dans votre classe principale
        mazeView.setAssociatedGraphView(graphView);
        graphView.setAssociatedMazeView(mazeView);

        // Initialiser le gestionnaire de sauvegarde
        this.saveManager = new SaveManager();
        this.saveView = new SaveView(saveManager);

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
        this.rowInput = new TextField();
        this.rowInput.setText("5");

        Text colLabel = new Text("Number of columns:");
        this.colInput = new TextField();
        this.colInput.setText("5");
        
        Text seedLabel = new Text("Seed:");
        this.seedInput = new TextField();
        this.seedInput.setText("42");


        // Create buttons
        this.clearButton = new Button("Clear");
        this.generateButton = new Button("Generate");
        this.saveButton = new Button("Save Maze");
        this.loadButton = new Button("Load Maze");
        this.animateGenerationButton = new Button("Step by step");

        //Give the same size to input fields
        this.rowInput.setPrefSize(100, 30);
        this.colInput.setPrefSize(100, 30);
        this.seedInput.setPrefSize(100, 30);
        this.clearButton.setPrefSize(100, 30);
        this.generateButton.setPrefSize(100, 30);
        this.saveButton.setPrefSize(100, 30);
        this.loadButton.setPrefSize(100, 30);
        this.animateGenerationButton.setPrefSize(100, 30);

        // Initialize algorithm label with centering
        Text algoLabel = new Text("Algorithms");
        algoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Create the toggle button for graph visibility
        this.toggleGraphButton = new Button("Hide Graph");
        this.toggleGraphButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");

        // Style the buttons
        this.saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        this.loadButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");

        // Create containers
        this.inputContainer = new VBox(10);
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);
        this.algoButtonContainer = new VBox(10);
        this.algoButtonContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
        
        // Add input fields and buttons to the input container
        inputContainer.getChildren().addAll(
            rowLabel, this.rowInput,
            colLabel, this.colInput,
            seedLabel, this.seedInput,
            this.generateButton,
            this.clearButton,
            this.saveButton,
            this.loadButton,
            this.toggleGraphButton,
            this.animateGenerationButton
        );

        // Add algorithm label to the algo container (buttons will be added by AlgorithmController)
        this.algoButtonContainer.getChildren().add(algoLabel);

        // Style the input container
        inputContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");

        // Create radio buttons for algorithm selection
        javafx.scene.control.RadioButton kruskalRadio = new javafx.scene.control.RadioButton("Kruskal");
        javafx.scene.control.RadioButton dfsRadio = new javafx.scene.control.RadioButton("DFS");
        kruskalRadio.setSelected(true); // Par défaut

        // Create a ToggleGroup for the radio buttons
        javafx.scene.control.ToggleGroup algoGroup = new javafx.scene.control.ToggleGroup();
        kruskalRadio.setToggleGroup(algoGroup);
        dfsRadio.setToggleGroup(algoGroup);

        // Add action listeners to the radio buttons
        kruskalRadio.setOnAction(e -> Graph.setGenerator(new KruskalGenerator()));
        dfsRadio.setOnAction(e -> Graph.setGenerator(new DFSGenerator()));

        // Create a container for the radio buttons
        javafx.scene.layout.HBox radioBox = new javafx.scene.layout.HBox(10, kruskalRadio, dfsRadio);
        radioBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Add the radio buttons to the input container
        inputContainer.getChildren().add(radioBox);
    }

    // Getters for the model and views
    protected Graph getModel() {
        return this.model;
    }
    
    protected void setModel(Graph model) {
        this.model = model;
    }
    
    protected GraphView getGraphView() {
        return this.graphView;
    }
    
    protected MazeView getMazeView() {
        return this.mazeView;
    }
    
    protected void setMazeView(MazeView mazeView) {
        this.mazeView = mazeView;
    }
    
    protected void updateMazeViewInContainer(MazeView newMazeView) {
        this.mazeContainer.getChildren().clear();
        this.mazeContainer.getChildren().add(newMazeView);
    }
    
    protected int getRowValue() {
        return this.rowInput.getText().equals("")? -1 : Integer.parseInt(this.rowInput.getText());
    }
    
    protected int getColumnValue() {
        return this.colInput.getText().equals("")? -1 : Integer.parseInt(this.colInput.getText());
    }
    
    protected int getSeedValue() {
        if (this.seedInput.getText().isEmpty()) {
            int seed = (int) (Math.random() * Integer.MAX_VALUE);
            this.seedInput.setText(String.valueOf(seed));
            return seed;
        } else {
            return Integer.parseInt(this.seedInput.getText());
        }
    }

    /**
     * Save the maze with the current settings.
     */
    private void saveMaze() {
        try {
            int rows = Integer.parseInt(this.rowInput.getText());
            int columns = Integer.parseInt(this.colInput.getText());
            int seed = Integer.parseInt(this.seedInput.getText());

            String mazeName = saveManager.saveMaze(rows, columns, seed);
            if (mazeName == null) {
                System.out.println("Ce labyrinthe existe déjà dans la liste sauvegardée.");
            } else {
                System.out.println("Labyrinthe sauvegardé sous: " + mazeName);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Erreur: Veuillez entrer des valeurs numériques valides.");
        }
    }

    /**
     * set up the containers for the graph and maze views.
     */
    private void setupContainers() {
        // Create titles for the graph and maze views
        Label graphTitle = new Label("Vue du Graphe");
        graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label mazeTitle = new Label("Vue du Labyrinthe");
        mazeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Set up the graph and maze containers
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10); 
        
        // Add the graph and maze views to their respective containers
        this.graphContainer.getChildren().addAll(graphTitle, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeTitle, this.mazeView);
        
        // Define the layout of the main container
        this.graphContainer.setPrefWidth(350);
        this.mazeContainer.setPrefWidth(350);
        this.graphContainer.setMinWidth(350);
        this.mazeContainer.setMinWidth(350);
        
        // Avoid resizing
        this.graphContainer.setAlignment(Pos.TOP_CENTER);
        this.mazeContainer.setAlignment(Pos.TOP_CENTER);

        // Style the graph and maze containers
        this.graphContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color:rgb(255, 254, 211);");
        this.mazeContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
    }
    
    /**
     * Set up actions for buttons.
     */
    private void setupButtonActions() {
        // Action for the generate button
        this.generateButton.setOnAction(e -> {
            // Validate input values
            if (this.rowInput.getText().isEmpty() || this.colInput.getText().isEmpty()) {
                System.out.println("Error: Please enter valid dimensions.");
                return;
            }
            else if (this.seedInput.getText().isEmpty()) {
                System.out.println("Error: Please enter a valid seed.");
                return;
            }
            //chose number lower than 25
            if (Integer.parseInt(this.rowInput.getText()) > 25 || Integer.parseInt(this.colInput.getText()) > 25) {
                System.out.println("Error: Dimensions must be less than 25.");
                return;
            }
            try {
                int rows = Integer.parseInt(this.rowInput.getText());
                int columns = Integer.parseInt(this.colInput.getText());
                int seed = getSeedValue();
                generateMaze(rows, columns, seed);
            } catch (NumberFormatException ex) {
                System.out.println("Error: Please enter valid numbers.");
            }
        });

        // Action for the clear button
        this.clearButton.setOnAction(e -> clearMaze());

        // Action for the save button
        this.saveButton.setOnAction(e -> saveMaze());

        // Action for the load button
        this.loadButton.setOnAction(e -> {
            if (!saveManager.hasSavedMazes()) {
                System.out.println("No saved mazes available.");
                return;
            }
            // Show the saved mazes window
            showSavedMazesWindow();
        });

        // Action for toggleGraphButton
        this.toggleGraphButton.setOnAction(e -> toggleGraphVisibility());
        
        // À modifier - cette méthode n'est jamais appelée directement mais seulement via le bouton
        // Il faut la remplacer par l'implémentation correcte dans AlgorithmController
        this.animateGenerationButton.setOnAction(e -> {
            if (this instanceof AlgorithmController) {
                ((AlgorithmController) this).animateMazeGeneration();
            } else {
                System.out.println("Animation non disponible - nécessite AlgorithmController");
            }
        });
    }

    /**
     * Show a window to display saved mazes and allow the user to load one.
     */
    private void showSavedMazesWindow() {
        saveView.showSavedMazesWindow((rows, columns, seed) -> {
            // Put the values in the input fields
            this.rowInput.setText(String.valueOf(rows));
            this.colInput.setText(String.valueOf(columns));
            this.seedInput.setText(String.valueOf(seed));
            
            // Create a new maze with the loaded values
            generateMaze(rows, columns, seed);
        });
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
        model.getAllNeighbours();
        this.mazeView = new MazeView(model, graphView);

        // updating container
        this.mazeContainer.getChildren().clear();
        this.mazeContainer.getChildren().add(this.mazeView);
        
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
        this.graphView.draw(model);
        this.mazeView.draw();

    }

    /**
     * Change the visibility of the graph.
     */
    private void toggleGraphVisibility() {
        graphVisible = !graphVisible;
        
        if (graphVisible) {
            // Display the graph
            this.graphContainer.setVisible(true);
            this.graphContainer.setManaged(true);
            this.toggleGraphButton.setText("Masquer le graphe");
        } else {
            // Hide the graph
            this.graphContainer.setVisible(false);
            this.graphContainer.setManaged(false);
            this.toggleGraphButton.setText("Afficher le graphe");
        }
    }

    // Getters for model and UI containers
    public VBox getInputContainer() {
        return this.inputContainer;
    }

    public VBox getGraphContainer() {
        return this.graphContainer;
    }

    public VBox getMazeContainer() {
        return this.mazeContainer;
    }

    public VBox getAlgoButtonContainer() {
        return this.algoButtonContainer;
    }
}