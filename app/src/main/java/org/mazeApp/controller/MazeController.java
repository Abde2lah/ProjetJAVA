package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;

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
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button DijkstraButton;
    private Button PrimButton;
    private Button KruskalButton;
    private Button RightButton;
    private Button LeftButton;
    private Button RandomButton;
    private VBox inputContainer;
    private VBox graphContainer;
    private VBox mazeContainer;
    private VBox algoButtonContainer;

    // Storage for saved mazes
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt"; // File to store saved mazes

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

        // Style the buttons
        this.saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        this.loadButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");

        // Create containers
        this.inputContainer = new VBox(10);
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);
        this.algoButtonContainer = new VBox(10);

        // Add input fields and buttons to the input container
        inputContainer.getChildren().addAll(
            rowLabel, this.rowInput,
            colLabel, this.colInput,
            seedLabel, this.seedInput,
            this.generateButton,
            this.clearButton,
            this.saveButton,
            this.loadButton,
            this.showSavedMazesButton
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
        inputContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
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
        // Set up the graph and maze containers
        this.graphContainer.getChildren().add(this.graphView);
        this.mazeContainer.getChildren().add(this.mazeView);

        // Style the graph and maze containers
        this.graphContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        this.mazeContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
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
    }

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
                generateMaze(savedMaze.getRows(), savedMaze.getColumns(), savedMaze.getSeed());
                System.out.println("Loaded maze: " + mazeName);
                savedMazesStage.close(); // Close the window after loading
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