package org.mazeApp;

import org.mazeApp.controller.MainControlleur;
import org.mazeApp.model.Graph;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Point d'entrée principal de l'application.
 */
public class Main extends Application {

    /**
     * La méthode start est le point d'entrée des applications JavaFX.
     * @param primaryStage La scène principale de l'application
     * @author Jeremy, Felipe, Abdellah, Sharov, Melanie
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Configuration de la fenêtre
            primaryStage.setTitle("CYNAPSE");
            
            // Chargement de l'icône
            Image icon = new Image("file:src/main/resources/icone.png");
            primaryStage.getIcons().add(icon);
            
            // Création du modèle et du contrôleur principal
            Graph graph = new Graph(42, 5, 5);
            MainControlleur mainController = new MainControlleur(graph);
            
            // Création de la scène avec la vue du contrôleur principal
            Scene scene = new Scene(mainController.getView(), 1200, 600);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(550);
            
            // Affichage de la fenêtre
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Erreur au démarrage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        launch(args);
    }
}