package org.mazeApp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;
import org.mazeApp.controller.MazeController;

public class Main extends Application {
    public GraphView graphView;
    public MazeView mazeView;
    public Graph graph;
    
    @Override
    public void start(Stage primaryStage) {
        // Configuration de la fenêtre
        Image icon = new Image("file:src/main/resources/icone.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Visualisation de Labyrinthe");
        
        // Créer un labyrinthe 5x5 par défaut
        this.graph = new Graph(42, 5);
        this.graphView = new GraphView();
        this.mazeView = new MazeView();
        
        // Initialiser les vues avec le modèle
        this.graphView.draw(this.graph);
        this.mazeView.draw(this.graph);
        
        // Créer le contrôleur qui contient toute l'UI
        MazeController controller = new MazeController(this.graph, this.graphView, this.mazeView);
        
        // Organiser la mise en page principale en utilisant les conteneurs du contrôleur
        HBox root = new HBox(20);
        root.getChildren().addAll(
            controller.getInputContainer(),
            controller.getGraphContainer(), 
            controller.getMazeContainer()
        );
        
        // Ajouter du style
        root.setStyle("-fx-padding: 10;");
        
        // Créer et afficher la scène
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}