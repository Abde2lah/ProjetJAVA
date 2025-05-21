package org.mazeApp.controller;

import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;
import org.mazeApp.view.SaveView;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Main controller for the maze application.
 * <p>
 * This class initializes and manages the core components of the application,
 * including the graph, maze, and their respective views. It also sets up the
 * algorithm and generation controllers, and handles UI container organization.
 * </p>
 * 
 * 
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
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

    /**
     * Returns the current graph model.
     *
     * @return the graph
     */
    protected Graph getModel() {
        return this.model;
    }

    /**
     * Sets the current graph model.
     *
     * @param model the new graph model
     */
    protected void setModel(Graph model) {
        this.model = model;
    }

    /**
     * Returns the GraphView instance.
     *
     * @return the graph view
     */    
    protected GraphView getGraphView() {
        return this.graphView;
    }

    /**
     * Returns the MazeView instance.
     *
     * @return the maze view
     */    
    protected MazeView getMazeView() {
        return this.mazeView;
    }

    /**
     * Sets the MazeView instance.
     *
     * @param mazeView the maze view to use
     */    
    protected void setMazeView(MazeView mazeView) {
        this.mazeView = mazeView;
    }

    /**
     * Replaces the current maze view in the container with a new one.
     *
     * @param newMazeView the new maze view
     */    
    protected void updateMazeViewInContainer(MazeView newMazeView) {
        this.mazeContainer.getChildren().clear();
        this.mazeContainer.getChildren().add(newMazeView);
    }

    /**
     * Sets up the VBox containers for the graph and maze views.
     */    
    private void setupContainers() {
        Label graphTitle = new Label("Vue du Graphe");
        graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label mazeTitle = new Label("Vue du Labyrinthe");
        mazeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        this.graphContainer = new VBox(5);
        this.mazeContainer = new VBox(5);

        this.graphContainer.getChildren().addAll(graphTitle, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeTitle, this.mazeView);

        this.graphContainer.setPrefWidth(250);
        this.graphContainer.setMinWidth(250);
        this.mazeContainer.setPrefWidth(600);
        this.mazeContainer.setMinWidth(600);

        this.graphContainer.setAlignment(Pos.TOP_CENTER);
        this.mazeContainer.setAlignment(Pos.TOP_CENTER);

        this.graphContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color:rgb(255, 254, 211);");
        this.mazeContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
    }


    /**
     * Toggles the visibility of the graph container in the UI.
     *
     * @param visible true to show the graph, false to hide it
     */
    public void toggleGraphVisibility(boolean visible) {
        if (visible) {
            graphContainer.setVisible(true);
            graphContainer.setManaged(true);
            mazeContainer.setPrefWidth(600);
        } else {
            graphContainer.setVisible(false);
            graphContainer.setManaged(false);
            mazeContainer.setPrefWidth(1000); 
        }
    }

    /**
     * Generates a new maze with the given dimensions and seed,
     * and updates the model and view.
     *
     * @param rows the number of rows in the maze
     * @param columns the number of columns in the maze
     * @param seed the seed for random generation
     */    
    public void generateMaze(int rows, int columns, int seed) {
        if (rows < 2 || columns < 2) {
            System.out.println("Error: Dimensions must be at least 2x2.");
            return;
        }
        System.out.println("Generating a " + rows + "x" + columns + " maze with seed " + seed);
        this.model = new Graph(seed, rows, columns);
        model.getAllNeighbours();
        this.mazeView = new MazeView(model, graphView);
        this.mazeContainer.getChildren().clear();
        this.mazeContainer.getChildren().add(this.mazeView);
        refreshViews();
    }

    /**
     * Clears the current maze from the graph and refreshes the views.
     */
    public void clearMaze() {
        System.out.println("Clearing the maze.");
        this.model.clearGraph();
        refreshViews();
    }

    /**
     * Refreshes the graph and maze views.
     */
    public void refreshViews() {
        this.graphView.draw(model);
        this.mazeView.draw();
    }

    /**
     * Returns the VBox containing the graph view.
     *
     * @return the graph container
     */
    public VBox getGraphContainer() {
        return this.graphContainer;
    }

    /**
     * Returns the VBox containing the maze view.
     *
     * @return the maze container
     */    
    public VBox getMazeContainer() {
        return this.mazeContainer;
    }

    /**
     * Returns a default row value (used as fallback).
     *
     * @return the row value
     */
    public int getRowValue() {
        return 5;
    }

    /**
     * Returns a default column value (used as fallback).
     *
     * @return the column value
     */
    public int getColumnValue() {
        return 5;
    }

    /**
     * Returns a default seed value (used as fallback).
     *
     * @return the seed value
     */
    public int getSeedValue() {
        return 42;
    }

    /**
     * Constructs the main UI layout as an HBox containing all major components:
     * generation controls, graph view, maze view, and algorithm controls.
     *
     * @return the assembled main view
     */
    public HBox getView() {
        AlgorithmController algoController = new AlgorithmController(model, this);
        GeneratorControlleur genController = new GeneratorControlleur(model, this);

        HBox mainView = new HBox(20);
        mainView.setAlignment(Pos.CENTER);
        mainView.getChildren().addAll(
            genController.getGenerationContainer(),
            this.graphContainer,
            this.mazeContainer,
            algoController.getAlgoContainer()
        );

        HBox.setHgrow(mazeContainer, Priority.ALWAYS); // Permit to the maze to extands itself
        mainView.setStyle("-fx-padding: 15; -fx-background-color: rgb(253, 255, 237);");
        return mainView;
    }

    /**
     * Returns the currently active graph model.
     *
     * @return the graph
     */
    public Graph getCurrentGraph() {
        return this.model;
    }

}
