package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class MazeView extends Pane {
    private double cellSize = 40; // Size of each cell in the maze
    private double padding = 20; // Padding around the maze
    private double wallThickness = 2.5; // Thickness of the walls

    /**
     * Draws the maze based on the given graph.
     * 
     * @param graph The graph representing the maze.
     */
    public void draw(Graph graph) {
        // Clear any previous maze
        getChildren().clear();

        // Get the number of rows and columns in the graph
        int rows = graph.getRows();
        int columns = graph.getColumns();

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

        // Remove walls based on the edges in the graph
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            ArrayList<Edges> edges = graph.getGraphMaze().get(i);
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
}