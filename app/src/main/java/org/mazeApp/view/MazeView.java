package org.mazeApp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.EditingView.MazeEditor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.util.Pair;


/**
 * MazeView is the visual representation of the maze.
 * <p>
 * It handles user interactions for editing the maze structure (e.g., adding/removing walls),
 * setting the start and end points, and animating pathfinding algorithm steps.
 * </p>
 *
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
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
    private MazeEditor mazeEditor;
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
        mazeEditor=new MazeEditor();
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Constructs a MazeView with the given graph and associated GraphView.
     *
     * @param graph the graph representing the maze
     * @param graphView the graph view used for synchronization with the maze view
     */
    public MazeView(Graph graph, GraphView graphView) {
        this.currentGraph = graph;
        this.associatedGraphView = graphView;
        mazeEditor=new MazeEditor();
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Define the associated graph view for synchronisation
     */
    public void setAssociatedGraphView(GraphView graphView) {
        this.associatedGraphView = graphView;
    }

    /**
     * Initialize visual properties of the view
     */
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

    /** 
    * Setup mouse interactions
    */
    private void setupEventHandlers() {
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Calcul the center of the maze
            double mazeWidth = columns * cellSize;
            double mazeHeight = rows * cellSize;
            double offsetX = (getWidth() - mazeWidth) / 2;
            double offsetY = (getHeight() - mazeHeight) / 2;

            // Reset hover on the walls
            wallHoverActive = false;
            checkWallHover(mouseX, mouseY, cellSize);
            if (!wallHoverActive && !bothPointsPlaced) {
                double gridX = mouseX - offsetX;
                double gridY = mouseY - offsetY;

                int col = (int) (gridX / cellSize);
                int row = (int) (gridY / cellSize);

                boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns &&
                                (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);

                if (isOnBorder) {
                    hoveredX = col * cellSize + cellSize / 2;
                    hoveredY = row * cellSize + cellSize / 2;
                } else {
                    hoveredX = -1;
                    hoveredY = -1;
                }
            }

            // Update cursor
            if (wallHoverActive) {
                setCursor(javafx.scene.Cursor.HAND);
            } else if (hoveredX >= 0) {
                setCursor(javafx.scene.Cursor.CROSSHAIR);
            } else {
                setCursor(javafx.scene.Cursor.DEFAULT);
            }

            draw();
        });

        // Clics gestion on the maze
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
     * @param mouseX X coordinates of the mouse
     * @param mouseY Y coordinates of the mouse
     * @param cellSize size of a cell
     * @return boolean if the mouse is near a wall
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

    /**
     * Verify the coordinates near of a wall
     * @param mouseX X coordinates of the mouse
     * @param mouseY Y coordinates of the mouse
     * @param cellSize size of a cell
     */ 
    private void checkWallHover(double mouseX, double mouseY, double cellSize) {
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        double gridX = mouseX - offsetX;
        double gridY = mouseY - offsetY;

        // Hover on a vertical wall
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

        // Hover on a horizonta wall
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
     * @param cell1 first cell
     * @param cell2 second cell 
     * @return if the cell are linked
     */
    private boolean areConnected(int cell1, int cell2) {
        return mazeEditor.areConnected(currentGraph, cell1, cell2);
    }


    /**
     * Monitor a click on a wall
     * @param mouseX X coordinates of the mouse
     * @param mouseY Y coordinates of the mouse
     * @param cellSize size of a cell
     */
    private void handleWallClick(double mouseX, double mouseY, double cellSize) {
        // Offset calcul
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
     * @param cell1 first cell
     * @param cell2 second cell
     * 
     */
    private void toggleWall(int cell1, int cell2) {
        if (mazeEditor.toggleConnection(currentGraph, cell1, cell2)) {
            draw();
            if (associatedGraphView != null) {
                associatedGraphView.draw(currentGraph);
            }
        }
    }

    
    /**
     * Draw the current graph
     */
    public void draw() {
        getChildren().clear();
        if (currentGraph == null) return;

        // Update the maze's dimesions
        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();

        double cellSize = calculateCellSize();
        double wallThickness = Math.max(minWallThickness, cellSize * 0.1);

        // Center the maze
        double mazeWidth = columns * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (getWidth() - mazeWidth) / 2;
        double offsetY = (getHeight() - mazeHeight) / 2;

        // say all the walls are present
        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];
        initializeWalls(horizontalWalls, verticalWalls);
        removeWallsBasedOnEdges(horizontalWalls, verticalWalls);
        drawWalls(horizontalWalls, verticalWalls, cellSize, wallThickness, offsetX, offsetY);
        drawSpecialPoints(cellSize, offsetX, offsetY);
    }


    /**
     * Verify the coordinates near of a wall
     * @param horizontalWalls list which contains the horizontals walls
     * @param verticalWalls list with all the vertical walls
     */
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

    /**
     * Verify the coordinates near of a wall
     * @param horizontalWalls list which contains the horizontals walls
     * @param verticalWalls list with all the vertical walls
     */
    private void removeWallsBasedOnEdges(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int source = edge.getSource();
                int dest = edge.getDestination();

                // Avoid duplication
                if (source >= dest) continue;

                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;

                if (sourceX == destX) {
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) {
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX+1] = false;
                }
            }
        }
    }

    /**
     * Verify the coordinates near of a wall
     * @param horizontalWalls list which contains the horizontals walls
     * @param verticalWalls list with all the vertical walls
     */
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

        // Hover add with success
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

    /**
     * Draw the special points with a good placement
     * @param cellSize size of a cell
     * @param offsetX offset on the X
     * @param offsetY offset on the Y to keep a good distance
     */
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


    /**
     * Calculate the best size for a cell
     * @return the minimal value
     */
    // Calcul the size of a cell
    private double calculateCellSize() {
        if (currentGraph == null) return 0;

        // Use all the heigth of the container
        double availableWidth = getWidth();
        double availableHeight = getHeight();
        double widthBased = availableWidth / columns;
        double heightBased = availableHeight / rows;

        return Math.min(widthBased, heightBased);
    }


    /**
     * Draw a path on the maze with a list of steps
     * @param path contains the node of the path's solution
     */
    public void drawPath(ArrayList<Integer> path) {
        if (path == null || path.isEmpty() || currentGraph == null) return;

        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);

        // Center calculation
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
     * @param steps collects all the steps of the resolution
     */
    public void visualiseStep(ArrayList<ArrayList<Integer>> steps) {
        if (steps.isEmpty()) {
            System.out.println("No paths found");
            return;
        }

        draw();

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
            System.out.println("Path found with a length of " + steps.get(steps.size() - 1).size());
        });

        this.currentAnimation = timeline;
        System.out.println("Timeline begin");
        timeline.play();
    }


    /**
     * visualize only solution
     * @param steps
     */
    public void nonAnimationVisualizeStep(ArrayList<ArrayList<Integer>> steps){
        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }
    
        draw(); 
    
        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);
    
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
    
        // Regrouper tous les bords de toutes les étapes
        HashSet<Pair<Integer, Integer>> allEdges = new HashSet<>();
        for (ArrayList<Integer> path : steps) {
            for (int i = 0; i < path.size() - 1; i++) {
                int from = path.get(i);
                int to = path.get(i + 1);
                allEdges.add(new Pair<>(Math.min(from, to), Math.max(from, to)));
            }
        }
    
        // Identifier les bords de la dernière étape
        HashSet<Pair<Integer, Integer>> lastStepEdges = new HashSet<>();
        ArrayList<Integer> lastStep = steps.get(steps.size() - 1);
        for (int i = 0; i < lastStep.size() - 1; i++) {
            int from = lastStep.get(i);
            int to = lastStep.get(i + 1);
            lastStepEdges.add(new Pair<>(Math.min(from, to), Math.max(from, to)));
        }
    
        // Colorier tous les bords en vert clair sauf les derniers en rouge
        for (Pair<Integer, Integer> edge : allEdges) {
            Line line = edgeLines.get(edge);
            if (line != null) {
                line.setStroke(lastStepEdges.contains(edge) ? Color.RED : Color.LIGHTGREEN);
                line.setVisible(true);
            }
        }
    
        System.out.println("Chemin trouvé de longueur " + lastStep.size());
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

    /**
     * Verify if there is a start and a ending point
     * @return if it's the case
     */
    public boolean verifyStartEnd(){
        if( startIndex == -1 || endIndex == -1 ){
            System.out.println("You musty be place a start and a end point");
            return false;
        }
        return true;
    }

    /**
     * get the start point
     * @return the start point
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * get the end point
     * @return the end point
     */
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
                System.out.println("Animation paused in the mazeview");
            } else {
                // Resume the animation
                currentAnimation.play();
                System.out.println("Animation rebegin at new dans MazeView");
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
     *clear all the animation 
     */
    public void clearAnimations() {
        // Stop all the timeline
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
                for (Circle circle : vertexCircles.values()) {
            circle.setFill(Color.WHITE);  
            circle.setVisible(false);  
        }
        
        // Reset the edges
        for (Line line : edgeLines.values()) {
            line.setStroke(Color.BLACK); 
            line.setStrokeWidth(1.0);     
        }
    
        // Clear the edges' status
        edgeStates.clear();
        
        // Show only the start and end point
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
        
        // Refresh the display
        requestLayout();
    }

    /**
     * Stop the current animation
     */
    public void stopAnimation() {
        // Stop the animation
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        animationPaused = false;
        
        draw();
    }

    /**
     * Refresh with force the view
     */
    public void refresh() {
        draw();
        if (associatedGraphView != null) {
            associatedGraphView.draw(currentGraph);
        }
    }
}