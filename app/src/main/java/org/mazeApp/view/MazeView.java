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
    private double cellSize = 40; // Size of each cell in the maze
    private double padding = 20; // Padding around the maze
    private double wallThickness = 2.5; // Thickness of the walls

    private int columns;
    private int rows;


    // hover variables
    private double hoveredX = -1;
    private double hoveredY = -1;

    // Variables for start and end points
    private int startIndex = -1;
    private int endIndex = -1;
    private boolean selectingStart = true;
    private boolean selectingEnd = true;

    private Graph currentGraph;

    public MazeView(Graph graph){

        this.currentGraph = graph;

        // Mouse hover listener for highlighting cells
        setOnMouseMoved(event -> {
            double mouseX = event.getX() - padding;
            double mouseY = event.getY() - padding;
        
            int col = (int) (mouseX / cellSize);
            int row = (int) (mouseY / cellSize);
        
            // verify that the hover is on the border
            if (row >= 0 && row < rows && col >= 0 && col < columns
                && (row == 0 || row == rows - 1 || col == 0 || col == columns - 1)) {
                hoveredX = col * cellSize + padding + cellSize / 2;
                hoveredY = row * cellSize + padding + cellSize / 2;
            } else {
                hoveredX = -1;
                hoveredY = -1;
            }
            draw(); // redraw to show hover
        });
        
        // Mouse click listener for placing start and end points
        setOnMouseClicked(event -> {
            if (hoveredX == -1 || hoveredY == -1) return;

            int col = (int) ((hoveredX - padding) / cellSize);
            int row = (int) ((hoveredY - padding) / cellSize);

            // verify that the click is on the border
            if (!(row == 0 || row == rows - 1 || col == 0 || col == columns - 1)) return;

            int index = row * columns + col;
            
            if (selectingStart) {
                startIndex = index;
                selectingStart = false; // Next click will place the end point
            } else if(selectingEnd) {
                endIndex = index;
                selectingEnd = false;
            }
            
            draw();
        });
        
    }

    /**
     * Draws the maze.
     * 
     */
    public void draw() {
        // Clear any previous maze
        getChildren().clear();


        // Get the number of rows and columns in the currentGraph
        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();

        // Initialize arrays to track horizontal and vertical walls
        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];

        // Set all walls to true initially
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

        // Remove walls based on the edges in the currentGraph
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            ArrayList<Edges> edges = currentGraph.getGraphMaze().get(i);
            for (Edges edge : edges) {
                int source = edge.getSource();
                int dest = edge.getDestination();

                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;

                // Remove horizontal or vertical walls based on the edge direction
                if (sourceX == destX) {
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) {
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX + 1] = false;
                }
            }
        }

        // Draw the maze background
        Rectangle background = new Rectangle(padding, padding, columns * cellSize, rows * cellSize);
        background.setFill(Color.WHITE);
        getChildren().add(background);

        // Draw horizontal walls
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (horizontalWalls[i][j]) {
                    double x1 = j * cellSize + padding;
                    double y1 = i * cellSize + padding;
                    double x2 = (j + 1) * cellSize + padding;

                    Line wall = new Line(x1, y1, x2, y1);
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }

        // Draw vertical walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                if (verticalWalls[i][j]) {
                    double x1 = j * cellSize + padding;
                    double y1 = i * cellSize + padding;
                    double y2 = (i + 1) * cellSize + padding;

                    Line wall = new Line(x1, y1, x1, y2);
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }

        // Draw start point (green)
        if (startIndex != -1) {
            int row = startIndex / columns;
            int col = startIndex % columns;
            double x = col * cellSize + padding + cellSize / 2;
            double y = row * cellSize + padding + cellSize / 2;

            Circle startCircle = new Circle(x, y, cellSize / 4, Color.GREEN);
            System.out.println("le start point est à :" + startIndex);
            this.getChildren().add(startCircle);
        }

        // Draw end point (red)
        if (endIndex != -1) {
            int row = endIndex / columns;
            int col = endIndex % columns;
            double x = col * cellSize + padding + cellSize / 2;
            double y = row * cellSize + padding + cellSize / 2;

            Circle endCircle = new Circle(x, y, cellSize / 4, Color.RED);
            System.out.println("le end point est à :" + endIndex);
            this.getChildren().add(endCircle);
        }

        // Draw hover circle (light blue)
        if (hoveredX != -1 && hoveredY != -1 && (selectingStart || selectingEnd)) {
            Circle hoverCircle = new Circle(hoveredX, hoveredY, cellSize / 4, Color.LIGHTBLUE);
            hoverCircle.setOpacity(0.5);
            this.getChildren().add(hoverCircle);
        }
    }

    /**
     * Clears the maze view.
     */
    public void clear() {
        getChildren().clear();
    }

    /**
     * Draws the solution path in the maze.
     * 
     * @param path The list of indices representing the solution path.
     */
    public void drawPath(ArrayList<Integer> path) {
        // Determine the number of columns based on the path
        int columns = 0;
        if (path.size() > 1) {
            int max = path.stream().max(Integer::compare).orElse(0);
            columns = (int) Math.sqrt(max + 1);
        }

        // Draw lines connecting each point in the path
        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);

            int currentRow = current / columns;
            int currentCol = current % columns;
            int nextRow = next / columns;
            int nextCol = next % columns;

            double x1 = currentCol * cellSize + padding + cellSize / 2;
            double y1 = currentRow * cellSize + padding + cellSize / 2;
            double x2 = nextCol * cellSize + padding + cellSize / 2;
            double y2 = nextRow * cellSize + padding + cellSize / 2;

            Line pathLine = new Line(x1, y1, x2, y2);
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(2.0);
            getChildren().add(pathLine);
        }
    }


    public void resetStartEndPoints() {
        startIndex = -1;
        endIndex = -1;
        selectingStart = true;
        draw();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}