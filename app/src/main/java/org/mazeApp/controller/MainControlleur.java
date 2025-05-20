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
        Label graphTitle = new Label("Vue du Graphe");
        graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label mazeTitle = new Label("Vue du Labyrinthe");
        mazeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);

        this.graphContainer.getChildren().addAll(graphTitle, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeTitle, this.mazeView);

        this.graphContainer.setPrefWidth(350);
        this.graphContainer.setMinWidth(350);
        this.mazeContainer.setPrefWidth(600);
        this.mazeContainer.setMinWidth(600);

        this.graphContainer.setAlignment(Pos.TOP_CENTER);
        this.mazeContainer.setAlignment(Pos.TOP_CENTER);

        this.graphContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color:rgb(255, 254, 211);");
        this.mazeContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
    }

    public void toggleGraphVisibility(boolean visible) {
        if (visible) {
            graphContainer.setVisible(true);
            graphContainer.setManaged(true);
            mazeContainer.setPrefWidth(600);
        } else {
            graphContainer.setVisible(false);
            graphContainer.setManaged(false);
            mazeContainer.setPrefWidth(1000); // Agrandit l'espace pour le labyrinthe
        }
    }

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

    public void clearMaze() {
        System.out.println("Clearing the maze.");
        this.model.clearGraph();
        refreshViews();
    }

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

    public int getRowValue() {
        return 5;
    }
    public int getColumnValue() {
        return 5;
    }
    public int getSeedValue() {
        return 42;
    }

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

        HBox.setHgrow(mazeContainer, Priority.ALWAYS); // Permet au labyrinthe de s'étendre
        mainView.setStyle("-fx-padding: 15; -fx-background-color: rgb(253, 255, 237);");
        return mainView;
    }
}
