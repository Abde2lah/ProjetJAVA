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

import javafx.scene.Cursor;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
    private Slider SpeedGenerationCursor;
    private Label SpeedGenerationLabel;
    private VBox generationContainer;
    private SaveManager saveManager;
    private SaveView saveView;
    private int delay = 5; // Delay in milliseconds for animation
    /**
     * Constructor for the generator controller
     * @param graph The graph model to be used in the application
     * @param mainController The main controller of the application 
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
        
        dfsRadio.setOnAction(e -> Graph.setGenerator(new DFSGenerator()));
        
        HBox radioBox = new HBox(10, kruskalRadio, dfsRadio, createImperfectMazeCB);
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
     * Implémentation de l'animation de génération du labyrinthe
     */
    public void animateMazeGeneration() {
        try {
            // Récupérer les valeurs numériques à partir des champs texte
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
            
            // Récupérer les étapes de génération
            ArrayList<Edges> steps = Graph.getCurrentGenerator().generate(rows, columns, seed);
            
            // Créer une nouvelle vue de labyrinthe
            MazeView animatedMazeView = new MazeView(animatedGraph, mainController.getGraphView());
            
            // Mettre à jour le modèle et la vue
            mainController.setModel(animatedGraph);
            mainController.setMazeView(animatedMazeView);
            mainController.updateMazeViewInContainer(animatedMazeView);
            
            // Créer la timeline pour l'animation
            Timeline timeline = new Timeline();
            
            // Ajouter chaque étape à la timeline avec le délai approprié
            for (int i = 0; i < steps.size(); i++) {
                final int index = i;
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                    if (index < steps.size()) {  // Vérification de sécurité
                        Edges edge = steps.get(index);
                        animatedGraph.addEdge(edge.getSource(), edge.getDestination());
                        animatedMazeView.draw();
                    }
                });
                timeline.getKeyFrames().add(frame);
            }
            
            // Ajouter un événement à la fin de l'animation
            timeline.setOnFinished(e -> System.out.println("Animation terminée"));
            
            // Lancer l'animation
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
            // Sauvegarder le labyrinthe avec sa structure complète
            Graph currentGraph = mainController.getModel();
            
            // Vérification que le modèle existe
            if (currentGraph == null) {
                System.out.println("Impossible de sauvegarder : aucun labyrinthe n'est généré.");
                return;
            }
            
            String mazeName = saveManager.saveMaze(currentGraph);
            
            if (mazeName == null) {
                System.out.println("Ce labyrinthe existe déjà dans la liste sauvegardée.");
            } else {
                System.out.println("Labyrinthe sauvegardé sous: " + mazeName);
            }
        } catch (Exception ex) {
            System.out.println("Erreur lors de la sauvegarde: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Load a saved maze
     */
    public void loadMaze() {
        if (!saveManager.hasSavedMazes()) {
            System.out.println("Aucun labyrinthe sauvegardé disponible.");
            return;
        }
        
        // Afficher la fenêtre des labyrinthes sauvegardés avec la nouvelle méthode
        saveView.showSavedMazesWindowEx((graph) -> {
            // Vérification que le graphe n'est pas null
            if (graph == null) {
                System.out.println("Erreur: le graphe chargé est null.");
                return;
            }
            
            // Mise à jour du modèle
            mainController.setModel(graph);
            
            // Mise à jour des champs d'entrée
            this.rowInput.setText(String.valueOf(graph.getRows()));
            this.colInput.setText(String.valueOf(graph.getColumns()));
            this.seedInput.setText(String.valueOf(graph.getSeed()));
            
            // Création d'une nouvelle vue de labyrinthe
            MazeView mazeView = new MazeView(graph, mainController.getGraphView());
            mainController.setMazeView(mazeView);
            
            // Mise à jour du conteneur
            mainController.updateMazeViewInContainer(mazeView);
            
            // Rafraîchissement des vues
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
