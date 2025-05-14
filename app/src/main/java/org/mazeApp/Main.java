package org.mazeApp;

import org.mazeApp.controller.MazeController;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    public GraphView graphView;
    public MazeView mazeView;
    public Graph graph;
    
    @Override
    public void start(Stage primaryStage) {
        // Setup of the windows
        Image icon = new Image("file:src/main/resources/icone.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Visualisation de Labyrinthe");
        
        // Create a graphe 5*5 by default with a seed of 42
        this.graph = new Graph(42, 5);
        this.graphView = new GraphView();
        this.mazeView = new MazeView();
        
        // Initialize the graph and maze views
        this.graphView.draw(this.graph);
        this.mazeView.draw(this.graph);
        
        // Create the controller
        MazeController controller = new MazeController(this.graph, this.graphView, this.mazeView);
        
        // Organize the layout
        HBox root = new HBox(20);
        root.getChildren().addAll(
            controller.getInputContainer(),
            controller.getGraphContainer(), 
            controller.getMazeContainer(),
            controller.getAlgobuttonContainer()
        );
        
        // Add style to the root container
        root.setStyle("-fx-padding: 10;");
        
        // Create the scene and set it to the stage
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}