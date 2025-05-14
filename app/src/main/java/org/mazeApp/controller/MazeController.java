package org.mazeApp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Main controller for the maze application.
 * This class handles the generation, clearing, saving, and loading of mazes,
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
    private Button saveButton;
    private Button loadButton;
    private Button showSavedMazesButton;
    private Button animateGenerationButton;
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button DijkstraButton;
    private Button PrimButton;
    private Button KruskalButton;
    private Button RightButton;
    private Button LeftButton;
    private Button RandomButton;
    private Button toggleGraphButton; // Ajouter un nouveau bouton pour afficher/masquer le graphe
    private VBox inputContainer;
    private VBox graphContainer;
    private VBox mazeContainer;
    private VBox algoButtonContainer;

    // Storage for saved mazes
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt"; // File to store saved mazes

    // Ajouter un attribut pour suivre l'état de visibilité du graphe
    private boolean graphVisible = true;

    /**
     * Class to represent a saved maze.
     */
    private static class SavedMaze {
        private final int seed;
        private final int rows;
        private final int columns;

        public SavedMaze(int seed, int rows, int columns) {
            this.seed = seed;
            this.rows = rows;
            this.columns = columns;
        }

        public int getSeed() {
            return seed;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }
    }
    //tool for the dfs step y step
    private static class DFSStep {
        int current;
        ArrayList<Integer> pathSoFar;

        DFSStep(int current, ArrayList<Integer> path) {
            this.current = current;
            this.pathSoFar = new ArrayList<>(path);
        }
    }


    public MazeController(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;

        // Initialize storage for saved mazes
        this.savedMazes = new HashMap<>();

        // Load mazes from the file
        loadMazesFromFile();

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
        this.saveButton = new Button("Save Maze");
        this.loadButton = new Button("Load Maze");
        this.showSavedMazesButton = new Button("Show Saved Mazes");
        this.animateGenerationButton = new Button("Animate Generation");

        // Initialize algorithm buttons
        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.DijkstraButton = new Button("Dijkstra");
        this.PrimButton = new Button("Prim");
        this.KruskalButton = new Button("Kruskal");
        this.RightButton = new Button("Right");
        this.LeftButton = new Button("Left");
        this.RandomButton = new Button("Random");

        // Créer le bouton pour afficher/masquer le graphe
        this.toggleGraphButton = new Button("Masquer le graphe");
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
            this.showSavedMazesButton,
            this.toggleGraphButton,
            this.animateGenerationButton
        );

        // Add algorithm buttons to the algo button container
        this.algoButtonContainer.getChildren().addAll(
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

        // Style the input container
        inputContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
    }

    /**
     * Save all mazes to a file for permanent storage.
     */
    private void saveMazesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String mazeName : savedMazes.keySet()) {
                SavedMaze savedMaze = savedMazes.get(mazeName);
                writer.write(String.format("%s,%d,%d,%d\n", mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns()));
            }
            System.out.println("Mazes saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving mazes to file: " + e.getMessage());
        }
    }

    /**
     * Load all mazes from a file.
     */
    private void loadMazesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String mazeName = parts[0];
                    int seed = Integer.parseInt(parts[1]);
                    int rows = Integer.parseInt(parts[2]);
                    int columns = Integer.parseInt(parts[3]);
                    savedMazes.put(mazeName, new SavedMaze(seed, rows, columns));
                }
            }
            System.out.println("Mazes loaded from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved mazes file found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading mazes from file: " + e.getMessage());
        }
    }

    /**
     * Save a maze and persist it to the file.
     */
    private void saveMaze() {
        String mazeName = "Maze_" + System.currentTimeMillis(); // Generate a unique name
        int rows = Integer.parseInt(this.rowInput.getText());
        int columns = Integer.parseInt(this.colInput.getText());
        int seed = Integer.parseInt(this.seedInput.getText());

        // Check for duplicates
        for (SavedMaze savedMaze : savedMazes.values()) {
            if (savedMaze.getSeed() == seed && savedMaze.getRows() == rows && savedMaze.getColumns() == columns) {
                System.out.println("This maze already exists in the saved list.");
                return;
            }
        }

        // Save the maze in memory and to the file
        savedMazes.put(mazeName, new SavedMaze(seed, rows, columns));
        saveMazesToFile(); // Persist the changes
        System.out.println("Maze saved as: " + mazeName);
    }

    //setupContainers
    private void setupContainers() {
        // Créer des titres pour chaque conteneur
        Label graphTitle = new Label("Vue du Graphe");
        graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label mazeTitle = new Label("Vue du Labyrinthe");
        mazeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Set up the graph and maze containers
        this.graphContainer = new VBox(10); // Espacement vertical
        this.mazeContainer = new VBox(10); // Espacement vertical
        
        // Ajouter les titres et vues aux conteneurs
        this.graphContainer.getChildren().addAll(graphTitle, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeTitle, this.mazeView);
        
        // Définir des tailles fixes pour les conteneurs
        this.graphContainer.setPrefWidth(350);
        this.mazeContainer.setPrefWidth(350);
        this.graphContainer.setMinWidth(350);
        this.mazeContainer.setMinWidth(350);
        
        // Empêcher la superposition en fixant des alignements
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
        this.clearButton.setOnAction(e -> clearMaze());

        // Action for the save button
        this.saveButton.setOnAction(e -> saveMaze());

        // Action for the load button
        this.loadButton.setOnAction(e -> {
            if (savedMazes.isEmpty()) {
                System.out.println("No saved mazes available.");
                return;
            }

            // Show the saved mazes window
            showSavedMazesWindow();
        });

        //Action to solve the maze with dfs
        this.DFSButton.setOnAction(e -> {
            int start = mazeView.getStartIndex();
            int end = mazeView.getEndIndex();

            if (start < 0 || end < 0) {
                System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
                return;
            }

            ArrayList<DFSStep> steps = getDFSVisitSteps(start, end);

            Timeline timeline = new Timeline();
            int delay = 100;

            for (int i = 0; i < steps.size(); i++) {
                DFSStep step = steps.get(i);
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e2 -> {
                    graphView.highlightVertex(step.current, model); // surligne le sommet
                });
                timeline.getKeyFrames().add(frame);
            }

            timeline.setOnFinished(e3 -> {
                // Quand l'animation est terminée, dessiner le chemin sur le maze
                mazeView.draw();
                mazeView.drawPath(steps.get(steps.size() - 1).pathSoFar);
            });

            timeline.play();
        });

        // Action pour le bouton de toggle du graphe
        this.toggleGraphButton.setOnAction(e -> toggleGraphVisibility());
        // action to slow down the maze generation
        this.animateGenerationButton.setOnAction(e -> playMazeGenerationStepByStep());

        };


    /**
     * Show a window to display saved mazes and allow the user to load one.
     */
    private void showSavedMazesWindow() {
        // Create a new stage (window)
        Stage savedMazesStage = new Stage();
        savedMazesStage.setTitle("Saved Mazes");

        // Create a ListView to display the saved mazes
        ListView<String> mazeListView = new ListView<>();
        for (String mazeName : savedMazes.keySet()) {
            SavedMaze savedMaze = savedMazes.get(mazeName);
            mazeListView.getItems().add(
                String.format("Name: %s | Seed: %d | Rows: %d | Columns: %d",
                    mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns())
            );
        }

        // Create a button to load the selected maze
        Button loadButton = new Button("Load Selected Maze");
        loadButton.setOnAction(e -> {
            String selectedMaze = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedMaze != null) {
                String mazeName = selectedMaze.split(" \\| ")[0].split(": ")[1];
                SavedMaze savedMaze = savedMazes.get(mazeName);

                // 💡 Met à jour les champs de saisie dans l'UI
                this.rowInput.setText(String.valueOf(savedMaze.getRows()));
                this.colInput.setText(String.valueOf(savedMaze.getColumns()));
                this.seedInput.setText(String.valueOf(savedMaze.getSeed()));

                // Génère le labyrinthe
                generateMaze(savedMaze.getRows(), savedMaze.getColumns(), savedMaze.getSeed());
                System.out.println("Loaded maze: " + mazeName);
                savedMazesStage.close(); // Ferme la fenêtre
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a maze to load.");
                alert.showAndWait();
            }
        });

        // Create a VBox layout and add the ListView and button
        VBox layout = new VBox(10, mazeListView, loadButton);
        layout.setStyle("-fx-padding: 10;");

        // Set up the scene and show the stage
        Scene scene = new Scene(layout, 400, 500);
        savedMazesStage.setScene(scene);
        savedMazesStage.show();
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
        this.mazeView = new MazeView(model);

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
        this.graphView.draw(this.model);
        this.mazeView.draw();
    }

    /**
     * Bascule la visibilité du graphe
     */
    private void toggleGraphVisibility() {
        graphVisible = !graphVisible;
        
        if (graphVisible) {
            // Afficher le graphe
            this.graphContainer.setVisible(true);
            this.graphContainer.setManaged(true);
            this.toggleGraphButton.setText("Masquer le graphe");
        } else {
            // Masquer le graphe
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

    public void playMazeGenerationStepByStep() {
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

            System.out.println("Animation du labyrinthe " + rows + "x" + columns + " avec seed " + seed);

            // Création du graphe complet juste pour récupérer les étapes
            Graph fullGraph = new Graph(seed, rows, columns);
            ArrayList<Edges> steps = fullGraph.getGenerationSteps();

            // Création d’un graphe vide pour l’animation
            Graph animatedGraph = Graph.emptyGraph(rows, columns);
            MazeView animatedMazeView = new MazeView(animatedGraph);

            this.model = animatedGraph;
            this.mazeView = animatedMazeView;

            this.mazeContainer.getChildren().clear();
            this.mazeContainer.getChildren().add(animatedMazeView);

            Timeline timeline = new Timeline();
            int delay = 50;

            for (int i = 0; i < steps.size(); i++) {
                Edges edge = steps.get(i);
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
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

    private ArrayList<DFSStep> getDFSVisitSteps(int start, int end) {
        ArrayList<DFSStep> steps = new ArrayList<>();
        boolean[] visited = new boolean[model.getVertexNb()];
        ArrayList<Integer> path = new ArrayList<>();
        dfsWithSteps(start, end, visited, path, steps);
        return steps;
    }

    // DFS récursif avec enregistrement des étapes
    private boolean dfsWithSteps(int current, int target, boolean[] visited,
                                ArrayList<Integer> path, ArrayList<DFSStep> steps) {
        visited[current] = true;
        path.add(current);
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
        //ne pas oublier de mettre un aspect temporel pour comparer les performances

        path.remove(path.size() - 1); // backtracking
        return false;
    }
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances
        //ne pas oublier de mettre un aspect temporel pour comparer les performances


}