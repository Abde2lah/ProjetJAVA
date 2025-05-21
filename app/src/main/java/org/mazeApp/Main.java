package org.mazeApp;

import org.mazeApp.controller.MainControlleur;
import org.mazeApp.model.Graph;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX entry point of the MazeApp application.
 * <p>
 * This class sets up the primary stage, loads the initial graph and controller,
 * and initializes the GUI window.
 * </p>
 * @author Jeremy, Felipe, Abdellah, Sharov, Melanie
 * @version 1.0
 */

public class Main extends Application {

    /**
     * JavaFX lifecycle method that sets up the main application window.
     *
     * @param primaryStage the primary window (stage) for this JavaFX application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Windows setup
            primaryStage.setTitle("CYNAPSE");
            
            // Icone loading
            Image icon = new Image("file:src/main/resources/icone.png");
            primaryStage.getIcons().add(icon);
            
            // Model and main controller creation
            Graph graph = new Graph(42, 5, 5);
            MainControlleur mainController = new MainControlleur(graph);
            
            // Scene with main controller view creation
            Scene scene = new Scene(mainController.getView(), 1200, 600);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(550);
            
            // Show the window
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error during the display " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}