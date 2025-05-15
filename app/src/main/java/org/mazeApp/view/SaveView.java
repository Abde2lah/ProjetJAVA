package org.mazeApp.view;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.mazeApp.model.SaveManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vue dédiée à l'interface de sauvegarde et chargement des labyrinthes
 */
public class SaveView {
    
    private SaveManager saveManager;
    
    public SaveView(SaveManager saveManager) {
        this.saveManager = saveManager;
    }
    
    /**
     * Affiche une fenêtre de sélection des labyrinthes sauvegardés
     * 
     * @param onLoadAction Action à exécuter lorsqu'un labyrinthe est chargé (reçoit rows, columns, seed)
     */
    public void showSavedMazesWindow(TriConsumer<Integer, Integer, Integer> onLoadAction) {
        // Create a new stage (window)
        Stage savedMazesStage = new Stage();
        savedMazesStage.setTitle("Saved Mazes");
        
        // Create a ListView to display the saved mazes
        ListView<String> mazeListView = new ListView<>();
        
        // Récupère les labyrinthes sauvegardés depuis SaveManager
        for (String mazeName : saveManager.getAllSavedMazes().keySet()) {
            SaveManager.SavedMaze savedMaze = saveManager.getSavedMaze(mazeName);
            mazeListView.getItems().add(
                String.format("Name: %s | Seed: %d | Rows: %d | Columns: %d",
                    mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns())
            );
        }
        
        // Create load and delete buttons
        Button loadButton = createLoadButton(mazeListView, savedMazesStage, onLoadAction);
        Button deleteButton = createDeleteButton(mazeListView);
        
        // Création d'un conteneur horizontal pour les boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loadButton, deleteButton);
        
        // Create a VBox layout and add the ListView and buttons
        VBox layout = new VBox(10, mazeListView, buttonBox);
        layout.setStyle("-fx-padding: 10;");
        
        // Set up the scene and show the stage
        Scene scene = new Scene(layout, 500, 500);
        savedMazesStage.setScene(scene);
        savedMazesStage.show();
    }
    
    /**
     * Crée le bouton pour charger un labyrinthe
     */
    private Button createLoadButton(ListView<String> mazeListView, Stage stage, 
                                  TriConsumer<Integer, Integer, Integer> onLoadAction) {
        Button loadButton = new Button("Load Selected Maze");
        loadButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loadButton.setPrefWidth(150);
        
        loadButton.setOnAction(e -> {
            String selectedMaze = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedMaze != null) {
                String mazeName = selectedMaze.split(" \\| ")[0].split(": ")[1];
                SaveManager.SavedMaze savedMaze = saveManager.getSavedMaze(mazeName);
                
                // Exécute la callback avec les paramètres du labyrinthe
                onLoadAction.accept(savedMaze.getRows(), savedMaze.getColumns(), savedMaze.getSeed());
                System.out.println("Loaded maze: " + mazeName);
                stage.close();
            } else {
                showWarningAlert("No Selection", "Please select a maze to load.");
            }
        });
        
        return loadButton;
    }
    
    /**
     * Crée le bouton pour supprimer un labyrinthe
     */
    private Button createDeleteButton(ListView<String> mazeListView) {
        Button deleteButton = new Button("Delete Selected Maze");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setPrefWidth(150);
        
        deleteButton.setOnAction(e -> {
            String selectedMaze = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedMaze != null) {
                String mazeName = selectedMaze.split(" \\| ")[0].split(": ")[1];
                
                // Affiche une confirmation avant la suppression
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation de suppression");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce labyrinthe ?");
                
                if (confirmAlert.showAndWait().get().getButtonData().isDefaultButton()) {
                    if (saveManager.deleteSavedMaze(mazeName)) {
                        // Rafraîchit la liste après suppression
                        mazeListView.getItems().remove(mazeListView.getSelectionModel().getSelectedIndex());
                        System.out.println("Maze deleted: " + mazeName);
                    }
                }
            } else {
                showWarningAlert("No Selection", "Please select a maze to delete.");
            }
        });
        
        return deleteButton;
    }
    
    /**
     * Affiche une alerte de type warning
     */
    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Interface fonctionnelle pour une fonction qui prend trois arguments
     */
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}