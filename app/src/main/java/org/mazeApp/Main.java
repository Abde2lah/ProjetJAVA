package org.mazeApp;

import org.mazeApp.controller.AlgorithmController;
import org.mazeApp.controller.MainControlleur;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    public Graph graph;
    
    @Override
    public void start(Stage primaryStage) {
        // Setup of the windows
        Image icon = new Image("file:src/main/resources/icone.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Visualisation de Labyrinthe");
        
        // Create a graph 5x5 by default with a seed of 42
        this.graph = new Graph(42, 5);
        
        // Create the controller
        AlgorithmController controller = new AlgorithmController(this.graph);

        // Organize the layout with specific spacing
        HBox root = new HBox(30); // Augmenter l'espacement horizontal
        root.setAlignment(Pos.CENTER); // Centrer les éléments horizontalement
        
        root.getChildren().addAll(
            controller.getInputContainer(),
            controller.getGraphContainer(), 
            controller.getMazeContainer(),
            controller.getAlgoButtonContainer() 
        );
        
        // Add style to the root container
        root.setStyle("-fx-padding: 15; -fx-background-color:rgb(253, 255, 237);");
        
        // Create the scene and set it to the stage - augmenter la taille
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}