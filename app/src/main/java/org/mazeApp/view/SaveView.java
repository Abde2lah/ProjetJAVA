package org.mazeApp.view;

import org.mazeApp.model.Graph;
import org.mazeApp.model.SaveManager;
import org.mazeApp.model.SaveManager.SavedMaze;

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
 * UI component for managing saved mazes.
 * <p>
 * The SaveView class provides a graphical interface to display, load, and delete saved mazes.
 * It uses a {@link SaveManager} to retrieve and manipulate saved maze data,
 * and presents it in a JavaFX window with buttons for user interaction.
 * </p>
 * 
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class SaveView {
    private SaveManager saveManager;

    /**
     * Constructs a SaveView with the given SaveManager.
     *
     * @param saveManager the save manager that handles maze persistence
     */
    public SaveView(SaveManager saveManager) {
        this.saveManager = saveManager;
    }


    /**
     * Display a window with saved mazes
     * 
     * @param onLoadGraphAction Callback to execute when a maze is loaded
     */
    public void showSavedMazesWindowEx(GraphConsumer onLoadGraphAction) {
        Stage savedMazesStage = new Stage();
        savedMazesStage.setTitle("Labyrinthes sauvegardés");
        // ListView for displaying saved mazes
        ListView<String> mazeListView = new ListView<>();
        //get all saved mazes
        for (String mazeName : saveManager.getAllSavedMazes().keySet()) {
            SavedMaze savedMaze = saveManager.getSavedMaze(mazeName);
            String displayText = String.format("Name: %s | Seed: %d | rows: %d | Columns: %d", mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns());
            mazeListView.getItems().add(displayText);
        }
        Button loadButton = createLoadGraphButton(mazeListView, savedMazesStage, onLoadGraphAction);
        Button deleteButton = createDeleteButton(mazeListView);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loadButton, deleteButton);
        VBox layout = new VBox(10, mazeListView, buttonBox);
        layout.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(layout, 500, 500);
        savedMazesStage.setScene(scene);
        savedMazesStage.show();
    }

    /**
     * Creates a button to load a maze from the list
     * @param mazeListView The ListView containing the saved mazes
     * @param stage The stage to close after loading the maze
     * @param onLoadGraphAction The action to perform after loading the maze
     * @return The load button
     */
    private Button createLoadGraphButton(ListView<String> mazeListView, Stage stage, 
                                      GraphConsumer onLoadGraphAction) {
        Button loadButton = new Button("load the selected maze");
        loadButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loadButton.setPrefWidth(200);
        
        loadButton.setOnAction(e -> {
            String selectedItem = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    // We extract the maze name from the selected item
                    String mazeName = selectedItem.split(" \\| ")[0].split(": ")[1];
                    // And we load the maze
                    Graph graph = saveManager.buildGraph(mazeName);
                    if (graph != null) {
                        // Exécution du callback avec le graphe complet
                        onLoadGraphAction.accept(graph);
                        System.out.println("Maze loaded: " + mazeName);
                        stage.close();
                    } else {
                        showWarningAlert("Error", "Impossible to load the maze");
                    }
                } catch (Exception ex) {
                    showWarningAlert("Error", "Problem during the maze's loading " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showWarningAlert("No selection", "Please select a maze in the list");
            }
        });
        
        return loadButton;
    }
    
    /**
     * This method creates a button to delete a maze from the list.
     * @param mazeListView The ListView containing the saved mazes
     * @return The delete button
     */
    private Button createDeleteButton(ListView<String> mazeListView) {
        Button deleteButton = new Button("Delete the selected maze");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteButton.setPrefWidth(200);
        
        deleteButton.setOnAction(e -> {
            String selectedItem = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    String mazeName = selectedItem.split(" \\| ")[0].split(": ")[1];
                    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Deleting confirmation");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Are you sure you want to delete this maze ?");
                    if (confirmAlert.showAndWait().get().getButtonData().isDefaultButton()) {
                        if (saveManager.deleteSavedMaze(mazeName)) {
                            // Refresh the ListView
                            mazeListView.getItems().remove(mazeListView.getSelectionModel().getSelectedIndex());
                            System.out.println("Maze deleted: " + mazeName);
                        }
                    }
                } catch (Exception ex) {
                    showWarningAlert("Error", "Problem during the suppression: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showWarningAlert("No selection", "Please select a maze to delete");
            }
        });
        
        return deleteButton;
    }
    
    /**
     * Display a warning alert with the given title and content.
     */
    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }    
    /**
     * Warning ! This is a functional interface
     * Explaination:
        * This interface is used to define a callback that accepts a Graph object.
        * It is used in the context of loading a graph from a saved maze.
        * The interface is functional because it contains only one abstract method.
        * This allows it to be used as a target for lambda expressions or method references.
     */
    @FunctionalInterface
    public interface GraphConsumer {
        void accept(Graph graph);
    }
}