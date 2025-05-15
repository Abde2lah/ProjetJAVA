package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class MazeView extends Pane {
    // Constantes d'affichage
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
    private final double padding = 2;
    private final double minWallThickness = 0.5;

    // État du labyrinthe
    private Graph currentGraph;
    private int rows, columns;
    private int startIndex = -1, endIndex = -1;
    private boolean selectingStart = true, selectingEnd = true;
    private double hoveredX = -1, hoveredY = -1;

    /**
     * Constructeur avec graphe initial
     * @param graph le graphe représentant le labyrinthe
     */
    public MazeView(Graph graph) {
        this.currentGraph = graph;
        initializeView();
        setupEventHandlers();
    }

    // Initialise les propriétés visuelles de la vue
    private void initializeView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }

    // Configure les interactions souris : survol et clic
    private void setupEventHandlers() {
        // Souris en mouvement : détecter survol des bords
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX() - padding;
            double mouseY = event.getY() - padding;

            int col = (int) (mouseX / cellSize);
            int row = (int) (mouseY / cellSize);

            boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns &&
                                 (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);

            if (isOnBorder) {
                hoveredX = col * cellSize + padding + cellSize / 2;
                hoveredY = row * cellSize + padding + cellSize / 2;
            } else {
                hoveredX = -1;
                hoveredY = -1;
            }

            draw();
        });

        // Clic souris : choisir point de départ/arrivée
        setOnMouseClicked(event -> {
            if (currentGraph == null || hoveredX < 0) return;

            double cellSize = calculateCellSize();
            int col = (int) ((hoveredX - padding) / cellSize);
            int row = (int) ((hoveredY - padding) / cellSize);

            if (!(row == 0 || row == rows - 1 || col == 0 || col == columns - 1)) return;

            int index = row * columns + col;

            if (selectingStart) {
                startIndex = index;
                selectingStart = false;
            } else if (selectingEnd) {
                endIndex = index;
                selectingEnd = false;
            }

            draw();
        });
    }

    /**
     * Dessine le labyrinthe actuel
     */
    public void draw() {
        getChildren().clear();
        if (currentGraph == null) return;

        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();
        double cellSize = calculateCellSize();
        double wallThickness = Math.max(minWallThickness, cellSize * 0.1);

        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];

        initializeWalls(horizontalWalls, verticalWalls);
        removeWallsBasedOnEdges(horizontalWalls, verticalWalls);

        drawBackground();
        drawWalls(horizontalWalls, verticalWalls, cellSize, wallThickness);
        drawSpecialPoints(cellSize);
    }

    // Initialise tous les murs à "présents"
    private void initializeWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                horizontalWalls[i][j] = true;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                verticalWalls[i][j] = true;
            }
        }
    }

    // Supprime les murs correspondant aux connexions du graphe
    private void removeWallsBasedOnEdges(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int source = edge.getSource();
                int dest = edge.getDestination();

                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;

                if (sourceX == destX) {
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) {
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX + 1] = false;
                }
            }
        }
    }

    // Dessine le fond du labyrinthe (rectangle blanc)
    private void drawBackground() {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        Rectangle background = new Rectangle(padding, padding, availableWidth, availableHeight);
        background.setFill(Color.WHITE);
        getChildren().add(background);
    }

    // Dessine les murs du labyrinthe
    private void drawWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls,
                           double cellSize, double wallThickness) {
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (horizontalWalls[i][j]) {
                    Line wall = new Line(
                        j * cellSize + padding,
                        i * cellSize + padding,
                        (j + 1) * cellSize + padding,
                        i * cellSize + padding
                    );
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                if (verticalWalls[i][j]) {
                    Line wall = new Line(
                        j * cellSize + padding,
                        i * cellSize + padding,
                        j * cellSize + padding,
                        (i + 1) * cellSize + padding
                    );
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }
    }

    // Dessine les points de départ, d’arrivée et le survol
    private void drawSpecialPoints(double cellSize) {
        double pointRadius = Math.max(0.5, cellSize / 4);

        if (startIndex >= 0) {
            int row = startIndex / columns;
            int col = startIndex % columns;
            Circle startCircle = new Circle(
                col * cellSize + padding + cellSize / 2,
                row * cellSize + padding + cellSize / 2,
                pointRadius, Color.GREEN
            );
            getChildren().add(startCircle);
        }

        if (endIndex >= 0) {
            int row = endIndex / columns;
            int col = endIndex % columns;
            Circle endCircle = new Circle(
                col * cellSize + padding + cellSize / 2,
                row * cellSize + padding + cellSize / 2,
                pointRadius, Color.RED
            );
            getChildren().add(endCircle);
        }

        if (hoveredX >= 0 && (selectingStart || selectingEnd)) {
            Circle hoverCircle = new Circle(hoveredX, hoveredY, pointRadius, Color.LIGHTBLUE);
            hoverCircle.setOpacity(0.5);
            getChildren().add(hoverCircle);
        }
    }

    // Calcule la taille d'une cellule en fonction de la grille et de l'espace
    private double calculateCellSize() {
        if (currentGraph == null) return 0;

        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        double widthBased = availableWidth / columns;
        double heightBased = availableHeight / rows;

        return Math.min(widthBased, heightBased);
    }

    /**
     * Trace un chemin sur le labyrinthe à partir d’une liste d’indices
     */
    public void drawPath(ArrayList<Integer> path) {
        if (path == null || path.isEmpty() || currentGraph == null) return;

        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);

        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);

            int row1 = current / columns;
            int col1 = current % columns;
            int row2 = next / columns;
            int col2 = next % columns;

            Line pathLine = new Line(
                col1 * cellSize + padding + cellSize / 2,
                row1 * cellSize + padding + cellSize / 2,
                col2 * cellSize + padding + cellSize / 2,
                row2 * cellSize + padding + cellSize / 2
            );
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(pathThickness);
            getChildren().add(pathLine);
        }
    }

    /**
     * Réinitialise les points de départ et d’arrivée
     */
    public void resetStartEndPoints() {
        startIndex = -1;
        endIndex = -1;
        selectingStart = true;
        selectingEnd = true;
        draw();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
