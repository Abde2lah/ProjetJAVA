package org.mazeApp.controller;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.model.generator.DFSGenerator;
import org.mazeApp.model.generator.KruskalGenerator;
import org.mazeApp.view.MazeView;
import org.mazeApp.view.SaveView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller responsible for maze generation.
 * <p>
 * This class manages the user interface components and logic 
 * for generating, animating, saving, and loading mazes in the application.
 * </p>
 * 
 * It handles user inputs such as dimensions and seed, and 
 * supports different maze generation algorithms (Kruskal and DFS).
 * 
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */

public class GeneratorControlleur {
    //main controller
    private MainControlleur mainController;
    // UI components
    private TextField rowInput;
    private TextField colInput;
    private TextField seedInput;
    private Button clearButton;
    private Button generateButton;
    private Button saveButton;
    private Button loadButton;
    private Button animateGenerationButton;
    private Button toggleGraphButton;
    private Slider SpeedGenerationCursor;
    private Label SpeedGenerationLabel;
    private VBox generationContainer;
    private SaveManager saveManager;
    private SaveView saveView;
    private int delay = 5; // Delay in milliseconds for animation


    /**
     * Constructs the GeneratorControlleur with the specified graph and main controller.
     *
     * @param graph the initial graph model
     * @param mainController the application's main controller
     */
    public GeneratorControlleur(Graph graph, MainControlleur mainController) {
        this.mainController = mainController;
        this.saveManager = new SaveManager();
        this.saveView = new SaveView(saveManager);
        initializeGeneratorControls();
        setupButtonActions();
    }
    
    /**
     * Initialize the generator UI components
     */
    private void initializeGeneratorControls() {
        // Creates label and inputs
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
        this.toggleGraphButton = new Button("Hide Graph");
        this.SpeedGenerationCursor = new Slider(1, 100, 5);
        this.SpeedGenerationLabel = new Label("Speed (delay each iteration) : "+delay+" ms");
        this.SpeedGenerationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        // Style of the buttons
        this.toggleGraphButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        this.saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        this.loadButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");


        // Set the preferred size of the buttons
        this.rowInput.setPrefSize(100, 30);
        this.colInput.setPrefSize(100, 30);
        this.seedInput.setPrefSize(100, 30);
        this.clearButton.setPrefSize(100, 30);
        this.generateButton.setPrefSize(100, 30);
        this.saveButton.setPrefSize(100, 30);
        this.loadButton.setPrefSize(100, 30);
        this.animateGenerationButton.setPrefSize(100, 30);
        this.SpeedGenerationCursor.setPrefSize(100, 30);
        
        RadioButton kruskalRadio = new RadioButton("Kruskal");
        RadioButton dfsRadio = new RadioButton("DFS");
        CheckBox createImperfectMazeCB = new CheckBox("Imperfect Maze");
        ToggleGroup algoGroup = new ToggleGroup();
        
        kruskalRadio.setSelected(true);
        kruskalRadio.setToggleGroup(algoGroup);
        
        dfsRadio.setToggleGroup(algoGroup);
        
        
        AtomicBoolean imperfectMzCheckBoxState = new AtomicBoolean(createImperfectMazeCB.isSelected());
        createImperfectMazeCB.setOnAction(e->{
          imperfectMzCheckBoxState.set(!imperfectMzCheckBoxState.get());
        });

        kruskalRadio.setOnAction(e -> Graph.setGenerator(new KruskalGenerator(imperfectMzCheckBoxState.get())));
        
        dfsRadio.setOnAction(e -> Graph.setGenerator(new DFSGenerator(imperfectMzCheckBoxState.get())));
        
        HBox radioBox = new HBox(10, kruskalRadio, dfsRadio);
        radioBox.setAlignment(Pos.CENTER);
        Text genTitle = new Text("Génération de Labyrinthe");
        genTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        // Main container for generation controls
        this.generationContainer = new VBox(10);
        this.generationContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");

        this.generationContainer.getChildren().addAll(
            genTitle,
            rowLabel, this.rowInput,
            colLabel, this.colInput,
            seedLabel, this.seedInput,
            radioBox,
            createImperfectMazeCB,
            this.generateButton,
            this.clearButton,
            this.saveButton,
            this.loadButton,
            this.toggleGraphButton,
            this.animateGenerationButton, 
            this.SpeedGenerationLabel,
            this.SpeedGenerationCursor
        );
    }
    
    /**
     * Setup actions for the buttons
     */
    private void setupButtonActions() {
        // Button for generating a maze
        this.generateButton.setOnAction(e -> generateMaze());
        // Button for clearing the maze
        this.clearButton.setOnAction(e -> clearMaze());
        // Buton for saving the maze
        this.saveButton.setOnAction(e -> saveMaze());
        // Button for loading a maze
        this.loadButton.setOnAction(e -> loadMaze());
        // Button for toggling the graph visibility
        this.toggleGraphButton.setOnAction(e -> toggleGraphVisibility());
        // Button for animating the maze generation
        this.animateGenerationButton.setOnAction(e -> animateMazeGeneration());
        
        // Correction: utiliser valueProperty().addListener pour mettre à jour le label en temps réel
        this.SpeedGenerationCursor.valueProperty().addListener((observable, oldValue, newValue) -> {
            delay = newValue.intValue();
            SpeedGenerationLabel.setText("Delay: " + delay + " ms");
        });
    }
    /**
     * Generate a new maze with the current settings
     */
    public void generateMaze() {
        try {
            int rows = getRowValue();
            int columns = getColumnValue();
            int seed = getSeedValue();
            if (verification(rows, columns, seed)==false) {
                return;
            }
            System.out.println("Generating a " + rows + "x" + columns + " maze with seed " + seed);
            // Create a new graph with the current settings
            Graph newModel = new Graph(seed, rows, columns);
            newModel.getAllNeighbours();

            // update the model in the main controller
            mainController.setModel(newModel);
            // Create a new maze view
            MazeView newMazeView = new MazeView(newModel, mainController.getGraphView());
            mainController.setMazeView(newMazeView);
            //update the maze view in the container
            mainController.updateMazeViewInContainer(newMazeView);
            mainController.refreshViews();
        } catch (NumberFormatException ex) {
            System.out.println("Error: Please enter valid numbers.");
        }
    }
    /**
     * Show the maze's animation during the generation
     */
    public void animateMazeGeneration() {
        try {
            // Récupthe numerals values froms the inputs
            int rows = getRowValue();
            int columns = getColumnValue();
            int seed = getSeedValue();
            
            if (!verification(rows, columns, seed)) {
                return;
            }
            
            System.out.println("Maze animation " + rows + "x" + columns + " with seed " + seed + " (delay: " + delay + "ms)");
            
            // Create an empty graph
            Graph animatedGraph = Graph.emptyGraph(rows, columns);
            animatedGraph.setSeed(seed);  // Définir la graine pour la cohérence
            
            // Recup the step of generation during this
            ArrayList<Edges> steps = Graph.getCurrentGenerator().generate(rows, columns, seed);
            
            // Create a new view of the maze
            MazeView animatedMazeView = new MazeView(animatedGraph, mainController.getGraphView());
            
            // Update model and view
            mainController.setModel(animatedGraph);
            mainController.setMazeView(animatedMazeView);
            mainController.updateMazeViewInContainer(animatedMazeView);
            
            // Create timeline for the view
            Timeline timeline = new Timeline();
            
            // Add each step with the asked delay
            for (int i = 0; i < steps.size(); i++) {
                final int index = i;
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                    if (index < steps.size()) {  
                        Edges edge = steps.get(index);
                        animatedGraph.addEdge(edge.getSource(), edge.getDestination());
                        animatedMazeView.draw();
                    }
                });
                timeline.getKeyFrames().add(frame);
            }
            
            timeline.setOnFinished(e -> System.out.println("Animation terminée"));
            
            // Launch the animation
            timeline.play();
            
        } catch (NumberFormatException e) {
            System.out.println("Error parsing input values: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Clear the current maze
     */
    public void clearMaze() {
        System.out.println("Clearing the maze.");
        mainController.getModel().clearGraph();
        mainController.refreshViews();
    }
    
    /**
     * Save the current maze
     */
    public void saveMaze() {
        try {
            // Save the maze
            Graph currentGraph = mainController.getModel();
            
            // Verify that mthe model already exists
            if (currentGraph == null) {
                System.out.println("Impossible to save");
                return;
            }
            
            String mazeName = saveManager.saveMaze(currentGraph);
            
            if (mazeName == null) {
                System.out.println("This maze already exists on the file");
            } else {
                System.out.println("Maze saved with the name " + mazeName);
            }
        } catch (Exception ex) {
            System.out.println("Error during the save" + ex.getMessage());
            ex.printStackTrace();

        }
    }
    
    /**
     * Load a saved maze
     */
    public void loadMaze() {
        if (!saveManager.hasSavedMazes()) {
            System.out.println("Non maze available");
            return;
        }
        
        saveView.showSavedMazesWindowEx((graph) -> {
            if (graph == null) {
                System.out.println("Error, the graph is null.");
                return;
            }
            
            // Update the model
            mainController.setModel(graph);
            
            // Update the inputs
            this.rowInput.setText(String.valueOf(graph.getRows()));
            this.colInput.setText(String.valueOf(graph.getColumns()));
            this.seedInput.setText(String.valueOf(graph.getSeed()));
            
            // Create new view for the maze
            MazeView mazeView = new MazeView(graph, mainController.getGraphView());
            mainController.setMazeView(mazeView);
            
            // Update Container
            mainController.updateMazeViewInContainer(mazeView);
            
            // Refresh view
            mainController.refreshViews();
        });
    }
    
    /**
     * Toggle the visibility of the graph view
     */
    private void toggleGraphVisibility() {
        boolean visible = mainController.getGraphContainer().isVisible();
        if (visible) {
            //hide the graph
            mainController.getGraphContainer().setVisible(false);
            mainController.getGraphContainer().setManaged(false);
            this.toggleGraphButton.setText("Afficher le graphe");
        } else {
            // show the graph
            mainController.getGraphContainer().setVisible(true);
            mainController.getGraphContainer().setManaged(true);
            this.toggleGraphButton.setText("Masquer le graphe");
        }
    }
    
    /**
     * Get the row value from the input field
     * @return the row value
     */
    private int getRowValue() {
        return this.rowInput.getText().equals("") ? -1 : Integer.parseInt(this.rowInput.getText());
    }
    
    /**
     * Get the column value from the input field
     * @return the column value
     */
    private int getColumnValue() {
        return this.colInput.getText().equals("") ? -1 : Integer.parseInt(this.colInput.getText());
    }
    
    /**
     * Get the seed value from the input field
     * @return the seed value
     */
    private int getSeedValue() {
        if (this.seedInput.getText().isEmpty()) {
            int seed = (int) (Math.random() * Integer.MAX_VALUE);
            this.seedInput.setText(String.valueOf(seed));
            return seed;
        } else {
            return Integer.parseInt(this.seedInput.getText());
        }
    }
    
    /**
     * Get the generation UI container
     */
    public VBox getGenerationContainer() {
        return this.generationContainer;
    }
    /**
     * Verification of the maze generation
     * @param rows The number of rows in the maze
     * @param columns The number of columns in the maze
     * @param seed The seed for the random number generator
     */
    public boolean verification(int rows, int columns, int seed) {
        if (rows <= 0 || columns <= 0) {
            System.out.println("Error: Please enter valid dimensions.");
            return false;
        }
        if (rows > 500 || columns > 500) {
            System.out.println("Error: Dimensions must be less than 500.");
            return false;
        }
        if (rows < 2 || columns < 2) {
            System.out.println("Error: Dimensions must be at least 2x2.");
            return false;
        }
        return true;
    }

}