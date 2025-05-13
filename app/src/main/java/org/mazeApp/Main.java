package org.mazeApp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;
import org.mazeApp.controller.MazeController;

public class Main extends Application {

    public GraphView graphView;
    public MazeView mazeView;
    public Graph graph;  // Cette variable doit être unique
    
    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image("file:src/main/resources/icone.png");
        primaryStage.getIcons().add(icon);
        // Création des champs de saisie
        Text rowLabel = new Text("Nombre de lignes :");
        TextField rowInput = new TextField("5");  // Valeur par défaut
        Text colLabel = new Text("Nombre de colonnes :");
        TextField colInput = new TextField("5");  // Valeur par défaut
        Text seedLabel = new Text("Graine :");
        TextField seedInput = new TextField("42");  // Valeur par défaut
        // Création des conteneurs
        VBox inputContainer = new VBox(10);
        VBox graphContainer = new VBox(10);
        VBox mazeContainer = new VBox(10);
        // Créer un labyrinthe 5x5 par défaut
        this.graph = new Graph(42, 5);  // Utiliser this.graph
        System.out.println(graph);
        this.graphView = new GraphView();
        this.mazeView = new MazeView();
        
        // Dessiner les vues initiales
        this.graphView.draw(this.graph); 
        this.mazeView.draw(this.graph);
        
        // Créer des étiquettes pour chaque vue
        Label graphLabel = new Label("Vue du Graphe");
        Label mazeLabel = new Label("Vue du Labyrinthe");
        
        // Ajouter les vues à leurs conteneurs
        graphContainer.getChildren().addAll(graphLabel, this.graphView);
        mazeContainer.getChildren().addAll(mazeLabel, this.mazeView);
        
        // Créer les boutons
        Button clearButton = new Button("Effacer");
        Button generateButton = new Button("Générer");
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(clearButton, generateButton);
        
        // Ajouter les champs de saisie et les boutons au conteneur d'entrée
        inputContainer.getChildren().addAll(
            rowLabel, rowInput, 
            colLabel, colInput, 
            seedLabel, seedInput,
            buttonContainer
        );
        
        // Dans la méthode start de votre classe Main
        MazeController controller = new MazeController(this.graph, this.graphView, this.mazeView);

        // Configurer les actions des boutons avec le contrôleur
        generateButton.setOnAction(e -> {
            try {
                int lignes = Integer.parseInt(rowInput.getText());
                int colonnes = Integer.parseInt(colInput.getText());
                int seed;
                
                if (seedInput.getText().isEmpty()) {
                    seed = (int) (Math.random() * Integer.MAX_VALUE);
                    seedInput.setText(String.valueOf(seed));
                } else {
                    seed = Integer.parseInt(seedInput.getText());
                }
                
                controller.generateMaze(lignes, colonnes, seed);
                this.graph = controller.getModel(); // Mettre à jour la référence au graphe
                
            } catch (NumberFormatException ex) {
                System.out.println("Erreur: Veuillez entrer des nombres valides");
            }
        });

        clearButton.setOnAction(e -> {
            controller.clearMaze();
            this.graph = controller.getModel(); // Mettre à jour la référence au graphe
        });
        
        // Organiser la mise en page principale
        HBox root = new HBox(20);
        root.getChildren().addAll(inputContainer, graphContainer, mazeContainer);
        
        // Ajouter du style
        root.setStyle("-fx-padding: 10;");
        graphLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        mazeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Créer et afficher la scène
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Visualisation de Labyrinthe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void refresh_view() {
        this.graphView.draw(this.graph);
        this.mazeView.draw(this.graph);    
    }
}