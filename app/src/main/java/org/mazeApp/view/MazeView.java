package org.mazeApp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

public class MazeView extends Pane {
    // Display constants
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
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
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }

    // Setup mouse interactions
    private void setupEventHandlers() {
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;
            
            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();
            
            // Reset the hover
            wallHoverActive = false;
            
            checkWallHover(mouseX, mouseY, cellSize);
            
            // Start and ending points hover
            if (!wallHoverActive && !bothPointsPlaced) {
                double gridX = mouseX - padding;
                double gridY = mouseY - padding;

                int col = (int) (gridX / cellSize);
                int row = (int) (gridY / cellSize);

                boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns &&
                                   (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);

                if (isOnBorder) {
                    hoveredX = col * cellSize + padding + cellSize / 2;
                    hoveredY = row * cellSize + padding + cellSize / 2;
                } else {
                    hoveredX = -1;
                    hoveredY = -1;
                }
            } else {
                hoveredX = -1;
                hoveredY = -1;
            }
            
            // Change the cursor depend of context
            if (wallHoverActive) {
                setCursor(javafx.scene.Cursor.HAND); 
            } else if (hoveredX >= 0) {
                setCursor(javafx.scene.Cursor.CROSSHAIR); 
            } else {
                setCursor(javafx.scene.Cursor.DEFAULT); 
            }
            
            draw();
        });

        // Mouse click to put a wall of chose a special point
        setOnMouseClicked(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();

            // If it's near a wall
            if (isNearWall(mouseX, mouseY, cellSize)) {
                System.out.println("Clic sur un mur détecté!");
                handleWallClick(mouseX, mouseY, cellSize);
                return;
            }

            if (hoveredX < 0 || bothPointsPlaced) return;
            
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

            if (!selectingStart && !selectingEnd) {
                bothPointsPlaced = true;
            }
        });
    }

    /**
     * Verify the coordinates near of a wall
     */
    private boolean isNearWall(double mouseX, double mouseY, double cellSize) {
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Vériify the proximity
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            // Calcul grid coordinates
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            // Veirify if the wall is vertical
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                return true;  // all the walls are updatables
            }
        }
        
        // Verify the proximity of a wall in the grid
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                return true;  // All the horizontales walls are updatables
            }
        }
        
        return false;
    }

    private void checkWallHover(double mouseX, double mouseY, double cellSize) {
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Verify the verticals walls
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            // Only for interior walls
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                hoveredWallX1 = gridCol * cellSize + padding;
                hoveredWallY1 = gridRow * cellSize + padding;
                hoveredWallX2 = gridCol * cellSize + padding;
                hoveredWallY2 = (gridRow + 1) * cellSize + padding;
                
                wallHoverActive = true;
                return;
            }
        }
        
        // Vérify the horizontal walls
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            
            // Interior wall only
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                // Coordinates of a wall chosen
                hoveredWallX1 = gridCol * cellSize + padding;
                hoveredWallY1 = gridRow * cellSize + padding;
                hoveredWallX2 = (gridCol + 1) * cellSize + padding;
                hoveredWallY2 = gridRow * cellSize + padding;
                
                wallHoverActive = true;
                return;
            }
        }
        
        // None walls hovered
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
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Detect a vertical wall
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                int cell1 = gridRow * columns + (gridCol - 1);
                int cell2 = gridRow * columns + gridCol;
                
                // Toggle the wall
                toggleWall(cell1, cell2);
            }
        }
        
        // Detection of an horizontal wall
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                int cell1 = (gridRow - 1) * columns + gridCol;
                int cell2 = gridRow * columns + gridCol;
                
                // Toggle a wall presence
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

    // Draw the white background of the maze
    private void drawBackground() {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        Rectangle background = new Rectangle(padding, padding, availableWidth, availableHeight);
        background.setFill(Color.WHITE);
        getChildren().add(background);
    }

    // Draw the walls of the maze
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
        
        // Aadd the hover on a wall
        if (wallHoverActive) {
            Line hoverLine = new Line(hoveredWallX1, hoveredWallY1, hoveredWallX2, hoveredWallY2);
            
            // Détermination if the wall is vertical or horizontal
            boolean isVertical = Math.abs(hoveredWallX1 - hoveredWallX2) < 0.01;
            
            int cell1, cell2;
            if (isVertical) {
                int gridCol = (int) Math.round((hoveredWallX1 - padding) / cellSize);
                int gridRow = (int) ((hoveredWallY1 - padding) / cellSize);
                cell1 = gridRow * columns + (gridCol - 1);
                cell2 = gridRow * columns + gridCol;
            } else {
                int gridRow = (int) Math.round((hoveredWallY1 - padding) / cellSize);
                int gridCol = (int) ((hoveredWallX1 - padding) / cellSize);
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

    // Calcul the size of a cell
    private double calculateCellSize() {
        if (currentGraph == null) return 0;

        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

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
     * Visualize step resolution
     */
    public void visualiseStep(ArrayList<ArrayList<Integer>> steps) {
        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }
    
        // Structure de données pour suivre l'état des arêtes
        // Map<arête (paire de sommets), état>
        // État: null = non visité, 1 = en cours de visite, 2 = visité précédemment
        Map<Pair<Integer, Integer>, Integer> edgeStates = new HashMap<>();
        
        // Pré-calculer la taille des cellules pour éviter de recalculer à chaque frame
        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);
        
        // Créer et stocker les objets graphiques à l'avance
        Map<Pair<Integer, Integer>, Line> edgeLines = new HashMap<>();
        // ajouter des sommets dans l'animations (à décommenter si nécessaire)
        // Map<Integer, Circle> vertexCircles = new HashMap<>();

        for (Edges edgeObj : currentGraph.getEdges()) {
            int from = edgeObj.getSource();
            int to = edgeObj.getDestination();
        
            int row1 = from / columns, col1 = from % columns;
            int row2 = to / columns, col2 = to % columns;
        
            double x1 = col1 * cellSize + padding + cellSize / 2;
            double y1 = row1 * cellSize + padding + cellSize / 2;
            double x2 = col2 * cellSize + padding + cellSize / 2;
            double y2 = row2 * cellSize + padding + cellSize / 2;
        
            Pair<Integer, Integer> edge = new Pair<>(Math.min(from, to), Math.max(from, to));
            Line line = new Line(x1, y1, x2, y2);
            line.setStrokeWidth(pathThickness);
            line.setVisible(false);
        
            edgeLines.put(edge, line);
            getChildren().add(line);
        }
        
    
        
        // Créer la Timeline
        Timeline timeline = new Timeline();
        
        for (int step = 0; step < steps.size(); step++) {
            ArrayList<Integer> currentPath = steps.get(step);
            final int finalStep = step;
            
            KeyFrame frame = new KeyFrame(Duration.millis(step * delay), e -> {
                // Dessiner le labyrinthe de base (une seule fois si c'est statique)
                if (finalStep == 0) {
                    if (associatedGraphView != null) {
                        associatedGraphView.draw(currentGraph);
                    }
                }
                
                // Mettre à jour l'état des arêtes
                // 1. Marquer toutes les arêtes actuellement à l'état 1 comme étant à l'état 2
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
                
                // 2. Marquer les arêtes du chemin actuel comme étant à l'état 1
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
                
                // // Mettre à jour l'état des sommets / ajouter des sommets dans l'animations (à décommenter si nécessaire)

                // if (associatedGraphView != null) {
                //     // Réinitialiser la visibilité des sommets
                //     for (Circle circle : vertexCircles.values()) {
                //         circle.setVisible(false);
                //     }
                    
                //     // Afficher les sommets du chemin actuel
                //     for (Integer vertex : path) {
                //         Circle circle = vertexCircles.get(vertex);
                //         if (circle != null) {
                //             circle.setFill(Color.RED);
                //             circle.setVisible(true);
                //         }
                //     }
                    
                //     // Afficher les sommets visités précédemment
                //     for (Pair<Integer, Integer> edge : edgeStates.keySet()) {
                //         if (edgeStates.get(edge) == 2) {
                //             Circle circle1 = vertexCircles.get(edge.getKey());
                //             Circle circle2 = vertexCircles.get(edge.getValue());
                            
                //             if (circle1 != null && !path.contains(edge.getKey())) {
                //                 circle1.setFill(Color.LIGHTGREEN);
                //                 circle1.setVisible(true);
                //             }
                            
                //             if (circle2 != null && !path.contains(edge.getValue())) {
                //                 circle2.setFill(Color.LIGHTGREEN);
                //                 circle2.setVisible(true);
                //             }
                //         }
                //     }
                // }
            });
            
            timeline.getKeyFrames().add(frame);
        }
        
        timeline.setOnFinished(e -> {
            System.out.println("Chemin trouvé de longueur " + steps.get(steps.size() - 1).size());
        });

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
}