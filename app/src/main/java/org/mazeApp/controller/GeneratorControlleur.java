package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.model.generator.DFSGenerator;
import org.mazeApp.model.generator.KruskalGenerator;
import org.mazeApp.view.MazeView;
import org.mazeApp.view.SaveView;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;

/**
 * Controller for the generation of mazes.
 * This class manages the UI for maze generation.
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
    private VBox generationContainer;
    private SaveManager saveManager;
    private SaveView saveView;

    /**
     * Constructor for the generator controller
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
        // Créer les étiquettes et champs de saisie
        Text rowLabel = new Text("Number of rows:");
        this.rowInput = new TextField();
        this.rowInput.setText("5");

        Text colLabel = new Text("Number of columns:");
        this.colInput = new TextField();
        this.colInput.setText("5");
    
        Text seedLabel = new Text("Seed:");
        this.seedInput = new TextField();
        this.seedInput.setText("42");

        // Créer les boutons
        this.clearButton = new Button("Clear");
        this.generateButton = new Button("Generate");
        this.saveButton = new Button("Save Maze");
        this.loadButton = new Button("Load Maze");
        this.animateGenerationButton = new Button("Step by step");
        this.toggleGraphButton = new Button("Hide Graph");
        
        // Styliser les boutons
        this.toggleGraphButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        this.saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        this.loadButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");

        // Définir les dimensions des champs et boutons
        this.rowInput.setPrefSize(100, 30);
        this.colInput.setPrefSize(100, 30);
        this.seedInput.setPrefSize(100, 30);
        this.clearButton.setPrefSize(100, 30);
        this.generateButton.setPrefSize(100, 30);
        this.saveButton.setPrefSize(100, 30);
        this.loadButton.setPrefSize(100, 30);
        this.animateGenerationButton.setPrefSize(100, 30);
        RadioButton kruskalRadio = new RadioButton("Kruskal");
        RadioButton dfsRadio = new RadioButton("DFS");
        kruskalRadio.setSelected(true);
        ToggleGroup algoGroup = new ToggleGroup();
        kruskalRadio.setToggleGroup(algoGroup);
        dfsRadio.setToggleGroup(algoGroup);
        kruskalRadio.setOnAction(e -> Graph.setGenerator(new KruskalGenerator()));
        dfsRadio.setOnAction(e -> Graph.setGenerator(new DFSGenerator()));
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
            this.generateButton,
            this.clearButton,
            this.saveButton,
            this.loadButton,
            this.toggleGraphButton,
            this.animateGenerationButton
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
        this.animateGenerationButton.setOnAction(e -> {
            AlgorithmController algoController = new AlgorithmController(mainController.getModel(), mainController);
            algoController.animateMazeGeneration();
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
            if (rows <= 0 || columns <= 0) {
                System.out.println("Error: Please enter valid dimensions.");
                return;
            }
            if (rows > 25 || columns > 25) {
                System.out.println("Error: Dimensions must be less than 25.");
                return;
            }
            if (rows < 2 || columns < 2) {
                System.out.println("Error: Dimensions must be at least 2x2.");
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

            // Rafraîchir les vues
            mainController.refreshViews();
            
        } catch (NumberFormatException ex) {
            System.out.println("Error: Please enter valid numbers.");
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
    private void saveMaze() {
        try {
            int rows = getRowValue();
            int columns = getColumnValue();
            int seed = getSeedValue();

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
     * Load a saved maze
     */
    private void loadMaze() {
        if (!saveManager.hasSavedMazes()) {
            System.out.println("No saved mazes available.");
            return;
        }
        
        // Afficher la fenêtre des labyrinthes sauvegardés
        saveView.showSavedMazesWindow((rows, columns, seed) -> {
            // Mettre les valeurs dans les champs d'entrée
            this.rowInput.setText(String.valueOf(rows));
            this.colInput.setText(String.valueOf(columns));
            this.seedInput.setText(String.valueOf(seed));
            
            // Générer un nouveau labyrinthe avec les valeurs chargées
            generateMaze();
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
}
