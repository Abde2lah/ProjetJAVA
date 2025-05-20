package org.mazeApp.model.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class UserPlaySolver {
    private final MazeView mazeView;
    private final Graph graph;
    private int currentIndex;
    private Circle playerCircle;
    private final Set<Integer> visited = new HashSet<>();
    private Label winLabel;

    public UserPlaySolver(MazeView mazeView, Graph graph) {
        this.mazeView = mazeView;
        this.graph = graph;
        this.currentIndex = mazeView.getStartIndex();

        // Création du label de victoire
        this.winLabel = new Label("🎉 GG vous avez réussi !");
        winLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: green; -fx-font-weight: bold;");
        winLabel.setVisible(false);
        mazeView.getChildren().add(winLabel); // On ajoute le label une seule fois
    }

    public void attachToScene() {
        if (currentIndex < 0) return;

        mazeView.setOnKeyPressed(this::handleKeyPress);
        mazeView.setFocusTraversable(true);
        mazeView.requestFocus();

        // Initialisation du label GG
        winLabel = new Label("🏁 GG WELL PLAY");
        winLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: green; -fx-font-weight: bold;");
        winLabel.setVisible(false); 
        mazeView.getChildren().add(winLabel);
        drawPlayer();
    }


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
                System.out.println("🏁 Vous avez atteint la sortie !");
                showVictoryLabel();
            }
        }
    }

    private boolean isConnected(int from, int to) {
        for (Edges edge : graph.getGraphMaze().get(from)) {
            if (edge.getDestination() == to) {
                return true;
            }
        }
        return false;
    }

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
            winLabel.setLayoutX(mazeView.getWidth() / 2 - 150); // Centré horizontalement
            winLabel.setLayoutY(mazeView.getHeight() - 50);     // Tout en bas
            winLabel.setVisible(true);
            if (!mazeView.getChildren().contains(winLabel)) {
                mazeView.getChildren().add(winLabel);
            }
        } else {
            winLabel.setVisible(false); // Cache le label si on repart
        }

    }



    // Affiche le message de victoire
    private void showVictoryLabel() {
        double centerX = mazeView.getWidth() / 2;
        double centerY = mazeView.getHeight() / 2;

        winLabel.setLayoutX(centerX - 150); // position ajustée
        winLabel.setLayoutY(centerY - 20);
        winLabel.setVisible(true);
    }
}
