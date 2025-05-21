package org.mazeApp.model.algorithms;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Allows a user to manually navigate through a maze using keyboard input (ZQSD).
 * Supports visual feedback in the MazeView and optional victory sound effects.
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class UserPlaySolver {
    private final MazeView mazeView;
    private final Graph graph;
    private int currentIndex;
    private Circle playerCircle;
    private final Set<Integer> visited = new HashSet<>();
    private Label winLabel;
    private boolean[] visitedArr; 
    // MediaPlayer pour jouer le son de victoire
    private MediaPlayer victoryPlayer;

    /**
     * Constructs the interactive solver and prepares the victory label and sound.
     *
     * @param mazeView the view used to render the maze and player
     * @param graph the maze structure to navigate
     */
    public UserPlaySolver(MazeView mazeView, Graph graph) {
        this.mazeView = mazeView;
        this.graph = graph;
        this.currentIndex = mazeView.getStartIndex();
        int vertexNb = graph.getVertexNb();
        this.visitedArr = new boolean[vertexNb];
        Arrays.fill(this.visitedArr,false);
        // Creates victory Label
        this.winLabel = new Label("GG WELL PLAYED !");
        winLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: green; -fx-font-weight: bold;");
        winLabel.setVisible(false);
        mazeView.getChildren().add(winLabel); 

        // Initialization of the victory sound effect
        URL soundUrl = getClass().getResource("/Victory.wav");
        if (soundUrl != null) {
            Media media = new Media(soundUrl.toExternalForm());
            victoryPlayer = new MediaPlayer(media);
            victoryPlayer.setOnError(() -> System.out.println("❌ Error can't listen the sound : " + victoryPlayer.getError()));
        }
    }

    /**
     * Attaches the solver to the MazeView and sets up key listeners for user control.
     * Initializes the player avatar and victory label.
     */
    public void attachToScene() {
        if (currentIndex < 0) return;

        mazeView.setOnKeyPressed(this::handleKeyPress);
        mazeView.setFocusTraversable(true);
        mazeView.requestFocus();

        winLabel = new Label("GG WELL PLAY");
        winLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: green; -fx-font-weight: bold;");
        winLabel.setVisible(false);
        mazeView.getChildren().add(winLabel);
        drawPlayer();
    }

    /**
     * Handles key press events to move the player through the maze using ZQSD controls.
     * Plays a victory sound and shows a label upon reaching the goal.
     *
     * @param event the KeyEvent triggered by user input
     */
    private void handleKeyPress(KeyEvent event) {
        if (currentIndex == mazeView.getEndIndex()) {
            System.out.println("GG WELL PLAY");
            return;
        }

        int row = currentIndex / graph.getColumns();
        int col = currentIndex % graph.getColumns();

        int targetIndex = -1;

        // Gestion ZQSD
        if (event.getCode() == KeyCode.Z) {
            targetIndex = (row > 0) ? (currentIndex - graph.getColumns()) : -1;
        } else if (event.getCode() == KeyCode.S) {
            targetIndex = (row < graph.getRows() - 1) ? (currentIndex + graph.getColumns()) : -1;
        } else if (event.getCode() == KeyCode.Q) {
            targetIndex = (col > 0) ? (currentIndex - 1) : -1;
        } else if (event.getCode() == KeyCode.D) {
            targetIndex = (col < graph.getColumns() - 1) ? (currentIndex + 1) : -1;
        }

        if (targetIndex != -1 && isConnected(currentIndex, targetIndex)) {
            currentIndex = targetIndex;
            drawPlayer();

            if (currentIndex == mazeView.getEndIndex()) {
                System.out.println("You reach the arrival");
                showVictoryLabel();
                playVictorySound();
            }
        }
    }

    /**
     * Checks whether there is a connection (edge) between two nodes.
     *
     * @param from the source node index
     * @param to the destination node index
     * @return true if a connection exists, false otherwise
     */
    private boolean isConnected(int from, int to) {
        for (Edges edge : graph.getGraphMaze().get(from)) {
            if (edge.getDestination() == to) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draws the player on the maze at the current index.
     * Recenters and redraws the maze view, showing victory if the player has reached the end.
     */
    private void drawPlayer() {
        mazeView.draw(); // redessine le labyrinthe => efface tout

        double cellSize = Math.min(
            mazeView.getWidth() / graph.getColumns(),
            mazeView.getHeight() / graph.getRows()
        );

        double mazeWidth = graph.getColumns() * cellSize;
        double mazeHeight = graph.getRows() * cellSize;
        double offsetX = (mazeView.getWidth() - mazeWidth) / 2;
        double offsetY = (mazeView.getHeight() - mazeHeight) / 2;

        int row = currentIndex / graph.getColumns();
        int col = currentIndex % graph.getColumns();

        double x = col * cellSize + offsetX + (cellSize / 2);
        double y = row * cellSize + offsetY + (cellSize / 2);

        playerCircle = new Circle(x, y, cellSize * 0.25, Color.BLUE);
        mazeView.getChildren().add(playerCircle);

        if (currentIndex == mazeView.getEndIndex()) {
            winLabel.setLayoutX(mazeView.getWidth() / 2 - 150);
            winLabel.setLayoutY(mazeView.getHeight() - 50);
            winLabel.setVisible(true);
            if (!mazeView.getChildren().contains(winLabel)) {
                mazeView.getChildren().add(winLabel);
            }
        } else {
            winLabel.setVisible(false);
        }
    }

    /**
     * Displays a congratulatory label in the center of the screen.
     */
    private void showVictoryLabel() {
        double centerX = mazeView.getWidth() / 2;
        double centerY = mazeView.getHeight() / 2;

        winLabel.setLayoutX(centerX - 150);
        winLabel.setLayoutY(centerY - 20);
        winLabel.setVisible(true);
    }

    /**
     * Plays the victory sound effect (if available).
     */
    private void playVictorySound() {
        if (victoryPlayer != null) {
            victoryPlayer.stop(); 
            victoryPlayer.play();
        }
    }
}
