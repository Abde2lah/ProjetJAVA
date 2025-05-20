package org.mazeApp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.util.Pair;

public class MazeView extends Pane {
    // Display constants
    private final double padding = 2;
    private final double minWallThickness = 0.5;

    // Maze status
    private Graph currentGraph;
    private int rows, columns;
    private int startIndex = -1, endIndex = -1;
    private boolean selectingStart = true, selectingEnd = true, bothPointsPlaced = false;
    private double hoveredX = -1, hoveredY = -1;
    
    // Maze attributes for editing
    private GraphView associatedGraphView;
    private double hoveredWallX1 = -1, hoveredWallY1 = -1, hoveredWallX2 = -1, hoveredWallY2 = -1;
    private boolean wallHoverActive = false;
    private int delay = 100; // Delay for animation

    // Ajouter ces attributs à la classe
    private boolean animationPaused = false;
    private Timeline currentAnimation = null;

    // Add missing declarations for animation state management
    private Map<Integer, Circle> vertexCircles = new HashMap<>();
    private Map<Pair<Integer, Integer>, Line> edgeLines = new HashMap<>();
    private Map<Pair<Integer, Integer>, Integer> edgeStates = new HashMap<>();

    /**
     * Constructor for the initial graph
     * @param graph the graph representing the maze
     */
    public MazeView(Graph graph) {
        this.currentGraph = graph;
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Constructeur avec graphe et vue graphe associée
     * @param graph le graphe représentant le labyrinthe
     * @param graphView la vue graphe associée
     */
    public MazeView(Graph graph, GraphView graphView) {
        this.currentGraph = graph;
        this.associatedGraphView = graphView;
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Define the associated graph view for synchronisation
     */
    public void setAssociatedGraphView(GraphView graphView) {
        this.associatedGraphView = graphView;
    }

    // Initialize visual properties of the view
    private void initializeView() {
        setPrefSize(900, 900);
        setMinSize(400, 400);  
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (getWidth() > 10 && getHeight() > 10) {
                draw();  
            }
        });
        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());
    }


    // Setup mouse interactions
    private void setupEventHandlers() {
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Calculer le centrage du labyrinthe
            double mazeWidth = columns * cellSize;
            double mazeHeight = rows * cellSize;
            double offsetX = (getWidth() - mazeWidth) / 2;
            double offsetY = (getHeight() - mazeHeight) / 2;

            // Réinitialiser l'état de survol des murs
            wallHoverActive = false;
            checkWallHover(mouseX, mouseY, cellSize);

            // Gestion du survol des points start/end
            if (!wallHoverActive && !bothPointsPlaced) {
                double gridX = mouseX - offsetX;
                double gridY = mouseY - offsetY;

                int col = (int) (gridX / cellSize);
                int row = (int) (gridY / cellSize);

                boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns &&
                                (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);

                if (isOnBorder) {
                    // ❗️Sans offset ici, car offset ajouté plus tard dans draw
                    hoveredX = col * cellSize + cellSize / 2;
                    hoveredY = row * cellSize + cellSize / 2;
                } else {
                    hoveredX = -1;
                    hoveredY = -1;
                }
            }


            // Mise à jour du curseur
            if (wallHoverActive) {
                setCursor(javafx.scene.Cursor.HAND);
            } else if (hoveredX >= 0) {
                setCursor(javafx.scene.Cursor.CROSSHAIR);
            } else {
                setCursor(javafx.scene.Cursor.DEFAULT);
            }

            draw();
        });

        // Gestion des clics sur le labyrinthe
        setOnMouseClicked(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();

            if (isNearWall(mouseX, mouseY, cellSize)) {
                handleWallClick(mouseX, mouseY, cellSize);
                return;
            }

            if (hoveredX < 0 || bothPointsPlaced) return;

            double mazeWidth = columns * cellSize;
            double mazeHeight = rows * cellSize;
            double offsetX = (getWidth() - mazeWidth) / 2;
            double offsetY = (getHeight() - mazeHeight) / 2;

            int col = (int) ((hoveredX) / cellSize);
            int row = (int) ((hoveredY) / cellSize);

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

            if (!selectingStart && !selectingEnd) {
                bothPointsPlaced = true;
            }
        });
    }


    /**
     * Verify the coordinates near of a wall
     */
    private boolean isNearWall(double mouseX, double mouseY, double cellSize) {
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        double gridX = mouseX - offsetX;
        double gridY = mouseY - offsetY;

        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                return true;
            }
        }

        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                return true;
            }
        }

        return false;
    }


    private void checkWallHover(double mouseX, double mouseY, double cellSize) {
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        double gridX = mouseX - offsetX;
        double gridY = mouseY - offsetY;

        // Hover sur mur vertical
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                hoveredWallX1 = gridCol * cellSize;
                hoveredWallY1 = gridRow * cellSize;
                hoveredWallX2 = gridCol * cellSize;
                hoveredWallY2 = (gridRow + 1) * cellSize;
                wallHoverActive = true;
                return;
            }
        }

        // Hover sur mur horizontal
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                hoveredWallX1 = gridCol * cellSize;
                hoveredWallY1 = gridRow * cellSize;
                hoveredWallX2 = (gridCol + 1) * cellSize;
                hoveredWallY2 = gridRow * cellSize;
                wallHoverActive = true;
                return;
            }
        }

        wallHoverActive = false;
    }


    
    /**
     * Verify if two walls are connected
     */
    private boolean areConnected(int cell1, int cell2) {
        if (cell1 < 0 || cell1 >= currentGraph.getVertexNb() || 
            cell2 < 0 || cell2 >= currentGraph.getVertexNb()) {
            return false;
        }
        
        for (Edges edge : currentGraph.getGraphMaze().get(cell1)) {
            if (edge.getDestination() == cell2) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Monitor a click on a wall
     */
    private void handleWallClick(double mouseX, double mouseY, double cellSize) {
        // Calcul de l’offset (centrage)
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        double gridX = mouseX - offsetX;
        double gridY = mouseY - offsetY;

        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                int cell1 = gridRow * columns + (gridCol - 1);
                int cell2 = gridRow * columns + gridCol;
                toggleWall(cell1, cell2);
            }
        }

        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                int cell1 = (gridRow - 1) * columns + gridCol;
                int cell2 = gridRow * columns + gridCol;
                toggleWall(cell1, cell2);
            }
        }
    }



    /**
     * Add or delete a wall
     */
    private void toggleWall(int cell1, int cell2) {
        if (cell1 < 0 || cell1 >= currentGraph.getVertexNb() || 
            cell2 < 0 || cell2 >= currentGraph.getVertexNb()) {
            return; 
        }
        
        if (areConnected(cell1, cell2)) {
            // If connected, delete the connection on the graph
            for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(cell1))) {
                if (edge.getDestination() == cell2) {
                    currentGraph.getGraphMaze().get(cell1).remove(edge);
                }
            }
            
            for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(cell2))) {
                if (edge.getDestination() == cell1) {
                    currentGraph.getGraphMaze().get(cell2).remove(edge);
                }
            }
            
            System.out.println("Mur ajouté entre " + cell1 + " et " + cell2);
        } else {
            //  If non connected, add a connection)
            currentGraph.getGraphMaze().get(cell1).add(new Edges(cell1, cell2));
            currentGraph.getGraphMaze().get(cell2).add(new Edges(cell2, cell1));
            
            System.out.println("Mur supprimé entre " + cell1 + " et " + cell2);
        }
        
        // Re-draw the maze
        draw();
        
        // Improve the maze view 
        if (associatedGraphView != null) {
            associatedGraphView.draw(currentGraph);
        }
    }
    
    /**
     * Draw the current graph
     */
    public void draw() {
        getChildren().clear();
        if (currentGraph == null) return;

        // Met à jour les dimensions du labyrinthe
        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();

        double cellSize = calculateCellSize();
        double wallThickness = Math.max(minWallThickness, cellSize * 0.1);

        // Calcul du centrage du labyrinthe
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        // Initialise tous les murs comme présents
        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];
        initializeWalls(horizontalWalls, verticalWalls);

        // Supprime les murs connectés selon le graphe
        removeWallsBasedOnEdges(horizontalWalls, verticalWalls);

        // Dessine les murs (y compris survol)
        drawWalls(horizontalWalls, verticalWalls, cellSize, wallThickness, offsetX, offsetY);

        // Dessine les points start, end, et le survol éventuel
        drawSpecialPoints(cellSize, offsetX, offsetY);
    }


    // Initialize all the presents walls 
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

    // Delete the walls and the connexions on the graphs
// Supprime les murs correspondants aux arêtes présentes dans le graphe
    private void removeWallsBasedOnEdges(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int source = edge.getSource();
                int dest = edge.getDestination();

                // Éviter les doublons : ne traiter l'arête qu'une seule fois
                if (source >= dest) continue;

                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;

                if (sourceX == destX) {
                    // Mur horizontal entre deux cellules verticalement adjacentes
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) {
                    // Mur vertical entre deux cellules horizontalement adjacentes
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX+1] = false;
                }
            }
        }
    }


// Draw the walls of the maze
    private void drawWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls,
                        double cellSize, double wallThickness,
                        double offsetX, double offsetY) {

        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (horizontalWalls[i][j]) {
                    Line wall = new Line(
                        j * cellSize + offsetX,
                        i * cellSize + offsetY,
                        (j + 1) * cellSize + offsetX,
                        i * cellSize + offsetY
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
                        j * cellSize + offsetX,
                        i * cellSize + offsetY,
                        j * cellSize + offsetX,
                        (i + 1) * cellSize + offsetY
                    );
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }

        // ✅ Ajout du survol sur un mur avec centrage correct
        if (wallHoverActive) {
            Line hoverLine = new Line(
                hoveredWallX1 + offsetX,
                hoveredWallY1 + offsetY,
                hoveredWallX2 + offsetX,
                hoveredWallY2 + offsetY
            );


            boolean isVertical = Math.abs(hoveredWallX1 - hoveredWallX2) < 0.01;

            int cell1, cell2;
            if (isVertical) {
                int gridCol = (int) Math.round((hoveredWallX1) / cellSize);
                int gridRow = (int) (hoveredWallY1 / cellSize);
                cell1 = gridRow * columns + (gridCol - 1);
                cell2 = gridRow * columns + gridCol;
            } else {
                int gridRow = (int) Math.round((hoveredWallY1) / cellSize);
                int gridCol = (int) (hoveredWallX1 / cellSize);
                cell1 = (gridRow - 1) * columns + gridCol;
                cell2 = gridRow * columns + gridCol;
            }

            boolean wallExists = !areConnected(cell1, cell2);

            hoverLine.setStrokeWidth(wallThickness * 2);
            hoverLine.setStroke(wallExists ? Color.RED : Color.GREEN);
            hoverLine.setOpacity(0.7);
            getChildren().add(hoverLine);
        }
    }


    // Draw the hover and special points on the maze
    private void drawSpecialPoints(double cellSize, double offsetX, double offsetY) {
        double pointRadius = Math.max(0.5, cellSize / 4);

        if (startIndex >= 0) {
            int row = startIndex / columns;
            int col = startIndex % columns;
            Circle startCircle = new Circle(
                col * cellSize + offsetX + cellSize / 2,
                row * cellSize + offsetY + cellSize / 2,
                pointRadius, Color.GREEN
            );
            getChildren().add(startCircle);
        }

        if (endIndex >= 0) {
            int row = endIndex / columns;
            int col = endIndex % columns;
            Circle endCircle = new Circle(
                col * cellSize + offsetX + cellSize / 2,
                row * cellSize + offsetY + cellSize / 2,
                pointRadius, Color.RED
            );
            getChildren().add(endCircle);
        }

        if (hoveredX >= 0 && (selectingStart || selectingEnd)) {
            Circle hoverCircle = new Circle(
                hoveredX + offsetX, hoveredY + offsetY, pointRadius, Color.LIGHTBLUE
            );
            hoverCircle.setOpacity(0.5);
            getChildren().add(hoverCircle);
        }
    }



    // Calcul the size of a cell
    private double calculateCellSize() {
        if (currentGraph == null) return 0;

        // Utilisation complète de la taille du conteneur
        double availableWidth = getWidth();
        double availableHeight = getHeight();

        // Choisir la plus petite taille pour que ça rentre
        double widthBased = availableWidth / columns;
        double heightBased = availableHeight / rows;

        return Math.min(widthBased, heightBased);
    }


    /**
     * Draw a path on the maze with a list of steps
     */
    public void drawPath(ArrayList<Integer> path) {
        if (path == null || path.isEmpty() || currentGraph == null) return;

        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);

        // Calcul du centrage
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);

            int row1 = current / columns;
            int col1 = current % columns;
            int row2 = next / columns;
            int col2 = next % columns;

            Line pathLine = new Line(
                col1 * cellSize + offsetX + cellSize / 2,
                row1 * cellSize + offsetY + cellSize / 2,
                col2 * cellSize + offsetX + cellSize / 2,
                row2 * cellSize + offsetY + cellSize / 2
            );
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(pathThickness);
            getChildren().add(pathLine);
        }
    }
    
    /**
     * Visualize step resolution
     */
    public void visualiseStep(ArrayList<ArrayList<Integer>> steps) {
        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }

        draw(); // Redessiner le labyrinthe

        Map<Pair<Integer, Integer>, Integer> edgeStates = new HashMap<>();

        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);

        // Calcul du centrage
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        Map<Pair<Integer, Integer>, Line> edgeLines = new HashMap<>();

        for (Edges edgeObj : currentGraph.getEdges()) {
            int from = edgeObj.getSource();
            int to = edgeObj.getDestination();

            int row1 = from / columns, col1 = from % columns;
            int row2 = to / columns, col2 = to % columns;

            double x1 = col1 * cellSize + offsetX + cellSize / 2;
            double y1 = row1 * cellSize + offsetY + cellSize / 2;
            double x2 = col2 * cellSize + offsetX + cellSize / 2;
            double y2 = row2 * cellSize + offsetY + cellSize / 2;

            Pair<Integer, Integer> edge = new Pair<>(Math.min(from, to), Math.max(from, to));
            Line line = new Line(x1, y1, x2, y2);
            line.setStrokeWidth(pathThickness);
            line.setVisible(false);

            edgeLines.put(edge, line);
            getChildren().add(line);
        }

        Timeline timeline = new Timeline();

        for (int step = 0; step < steps.size(); step++) {
            final int finalStep = step;

            KeyFrame frame = new KeyFrame(Duration.millis(step * delay), e -> {
                if (finalStep == 0 && associatedGraphView != null) {
                    associatedGraphView.draw(currentGraph);
                }

                for (Map.Entry<Pair<Integer, Integer>, Integer> entry : edgeStates.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() == 1) {
                        edgeStates.put(entry.getKey(), 2);
                        Line line = edgeLines.get(entry.getKey());
                        if (line != null) {
                            line.setStroke(Color.LIGHTGREEN);
                            line.setVisible(true);
                        }
                    }
                }

                ArrayList<Integer> path = steps.get(finalStep);
                for (int i = 0; i < path.size() - 1; i++) {
                    int from = path.get(i);
                    int to = path.get(i + 1);
                    Pair<Integer, Integer> edge = new Pair<>(Math.min(from, to), Math.max(from, to));
                    edgeStates.put(edge, 1);

                    Line line = edgeLines.get(edge);
                    if (line != null) {
                        line.setStroke(Color.RED);
                        line.setVisible(true);
                    }
                }
            });

            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            System.out.println("Chemin trouvé de longueur " + steps.get(steps.size() - 1).size());
        });

        this.currentAnimation = timeline;
        System.out.println("Timeline démarrée");
        timeline.play();
    }

    /**
     * Reset Start and end points
     */
    public void resetStartEndPoints() {
        startIndex = -1;
        endIndex = -1;
        selectingStart = true;
        selectingEnd = true;
        bothPointsPlaced = false;
        draw();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    /**
     * Set the delay for the animation resolver
     * @param delay
     */
    public void setDelayResolverAnimation(int delay) {
        this.delay = delay;
    }

    /**
     * Set the animation paused or resumed
     * @param paused true to pause the animation, false to resume
     */
    public void setAnimationPaused(boolean paused) {
        this.animationPaused = paused;
        
        if (currentAnimation != null) {
            if (paused) {
                // Pause the animation
                currentAnimation.pause();
                System.out.println("Animation mise en pause dans MazeView");
            } else {
                // Resume the animation
                currentAnimation.play();
                System.out.println("Animation reprise dans MazeView");
            }
        }
    }
    
    /**
     * Check if the animation is currently running
     * @return true if an animation is in progress, false otherwise
     */
    public boolean isAnimationRunning() {
        return currentAnimation != null && 
               (currentAnimation.getStatus() == javafx.animation.Animation.Status.RUNNING || 
                currentAnimation.getStatus() == javafx.animation.Animation.Status.PAUSED);
    }
    
    /**
     * Check if the animation is currently paused
     * @return true if the animation is paused, false otherwise
     */
    public boolean isAnimationPaused() {
        return animationPaused;
    }
    
    /**
     * Efface toutes les animations et réinitialise l'affichage
     */
    public void clearAnimations() {
        // Arrêter toute Timeline en cours
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        
        // Réinitialiser la vue graphique
        for (Circle circle : vertexCircles.values()) {
            circle.setFill(Color.WHITE);  // Couleur par défaut
            circle.setVisible(false);     // Cacher tous les cercles
        }
        
        // Réinitialiser les arêtes
        for (Line line : edgeLines.values()) {
            line.setStroke(Color.BLACK);  // Couleur par défaut
            line.setStrokeWidth(1.0);     // Largeur par défaut
        }
        
        // Effacer les états des arêtes
        edgeStates.clear();
        
        // Afficher uniquement les points de départ et d'arrivée
        if (startIndex >= 0 && vertexCircles.containsKey(startIndex)) {
            Circle startCircle = vertexCircles.get(startIndex);
            startCircle.setFill(Color.GREEN);
            startCircle.setVisible(true);
        }
        
        if (endIndex >= 0 && vertexCircles.containsKey(endIndex)) {
            Circle endCircle = vertexCircles.get(endIndex);
            endCircle.setFill(Color.RED);
            endCircle.setVisible(true);
        }
        
        // Rafraîchir l'affichage
        requestLayout();
    }

    /**
     * Arrête complètement l'animation en cours
     */
    public void stopAnimation() {
        // Arrêter l'animation en cours
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        
        // Nettoyer les états
        animationPaused = false;
        
        // Redessiner le labyrinthe
        draw();
    }

    /**
     * Force le rafraîchissement complet de la vue
     */
    public void refresh() {
        // Redessiner le labyrinthe
        draw();
        
        // Si un graphe associé existe, le redessiner aussi
        if (associatedGraphView != null) {
            associatedGraphView.draw(currentGraph);
        }
    }
}