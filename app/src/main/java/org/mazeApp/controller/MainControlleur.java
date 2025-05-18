package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;
import org.mazeApp.view.SaveView;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * Main controller for the maze application.
 * This class initialize AlgorithmController and GeneratorController.
 * It also manages the graph and maze views.
 * It is responsible for generating and clearing the maze.
 */
public class MainControlleur {

    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    private SaveManager saveManager;
    private VBox graphContainer;
    private VBox mazeContainer;


    /**
     * Constructor for the MainControlleur class.
     * Initializes the model, graph view, maze view, and save manager.
     * Sets up the containers for the graph and maze views.
     *
     * @param graph The graph model to be used in the application
     */
    public MainControlleur(Graph graph) {
        this.model = graph;
        this.graphView = new GraphView();
        this.mazeView = new MazeView(model, graphView);
        this.graphView.draw(model);
        this.mazeView.draw();
        mazeView.setAssociatedGraphView(graphView);
        graphView.setAssociatedMazeView(mazeView);
        this.saveManager = new SaveManager();
        new SaveView(saveManager);
        new AlgorithmController(graph, this);
        new GeneratorControlleur(graph, this);
        setupContainers();
    }
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
     * Generate a new maze with the given settings.
     * @param rows The number of rows in the maze
     * @param columns The number of columns in the maze
     * @param seed The seed for the random number generator
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
     * This method clears the graph and refreshes the views.
     */
    public void clearMaze() {
        System.out.println("Clearing the maze.");
        this.model.clearGraph();
        refreshViews();
    }
    /**
     * Refresh the views of the graph and maze.
     */
    public void refreshViews() {
        this.graphView.draw(model);
        this.mazeView.draw();

    }
    public VBox getGraphContainer() {
        return this.graphContainer;
    }
    public VBox getMazeContainer() {
        return this.mazeContainer;
    }
    /**
     * if there is no maze, return default values
     * @return The row value
     */
    public int getRowValue() {
        return 5;
    }
    /**
     * if there is no maze, return default values
     * @return The column value
     */
    public int getColumnValue() {
        return 5;
    }
    /**
     * if there is no maze, return default values
     * @return getSeedValue
     */
    public int getSeedValue() {
        return 42;
    }
    /**
     * The getView method creates the main view of the application.
     * It initializes the child controllers and adds them to the main container.
     * @return a HBox containing the main view of the application
     */
    public HBox getView() {
        //create the controllers of the application
        AlgorithmController algoController = new AlgorithmController(model, this);
        GeneratorControlleur genController = new GeneratorControlleur(model, this);
        // Create the main view : a horizontal box
        HBox mainView = new HBox(20);
        mainView.setAlignment(Pos.CENTER);
        mainView.getChildren().addAll(
            genController.getGenerationContainer(),
            this.graphContainer,
            this.mazeContainer,
            algoController.getAlgoContainer()
        );
        mainView.setStyle("-fx-padding: 15; -fx-background-color: rgb(253, 255, 237);");
        return mainView;
    }

}