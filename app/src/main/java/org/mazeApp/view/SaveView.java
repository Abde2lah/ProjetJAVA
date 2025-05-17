package org.mazeApp.view;

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
 * View dedicated of the loading and saving maze 
 */
public class SaveView {
    
    private SaveManager saveManager;
    
    public SaveView(SaveManager saveManager) {
        this.saveManager = saveManager;
    }
    
    /**
     * Show a windows with mazes
     * 
     * @param onLoadAction Action to execute to collect maze's informations
     */
    public void showSavedMazesWindow(TriConsumer<Integer, Integer, Integer> onLoadAction) {
        // Create a new stage (window)
        Stage savedMazesStage = new Stage();
        savedMazesStage.setTitle("Saved Mazes");
        
        // Create a ListView to display the saved mazes
        ListView<String> mazeListView = new ListView<>();
        
        // Collect the saved mazes
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
        
        // Create a container for the buttons
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
     * Create the button to load a maze
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
                
                // Exécute the callback with maze's settings
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
     * Create a button to delete a maze
     */
    private Button createDeleteButton(ListView<String> mazeListView) {
        Button deleteButton = new Button("Delete Selected Maze");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setPrefWidth(150);
        
        deleteButton.setOnAction(e -> {
            String selectedMaze = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedMaze != null) {
                String mazeName = selectedMaze.split(" \\| ")[0].split(": ")[1];
                
                // Show a confirmation before the suppression
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Deleting confirmation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Are you sure you want to delete this maze");
                
                if (confirmAlert.showAndWait().get().getButtonData().isDefaultButton()) {
                    if (saveManager.deleteSavedMaze(mazeName)) {
                        // Refresh the list after deleting
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
     * Warning alert display
     */
    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Fonctional interface 
     */
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}