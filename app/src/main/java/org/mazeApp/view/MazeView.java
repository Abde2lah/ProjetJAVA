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
    private double padding = 20;
    private double wallThickness = 2.5; // Thick value for walls

    public void draw(Graph graph) {
        // Erase the previous maze
        getChildren().clear();
        
        // Recup the data given by the user
        int rows = graph.getRows();
        int columns = graph.getColumns();
        // Create the walls
        boolean[][] horizontalWalls = new boolean[rows+1][columns]; // Walls horizontally
        boolean[][] verticalWalls = new boolean[rows][columns+1];   // Walls vertically
        
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                horizontalWalls[i][j] = true;
            }
        }
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= rows; j++) {
                verticalWalls[i][j] = true;
            }
        }
        
        // Delete walls between cells based on the edges in the graph
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            ArrayList<Edges> edges = graph.getGraphMaze().get(i);
            for (Edges edge : edges) {
                int source = edge.getSource();
                int dest = edge.getDestination();
                
                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;
                
                // Put off the wall between the two cells
                if (sourceX == destX) { 
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY+1][sourceX] = false; 
                } else if (sourceY == destY) { 
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX+1] = false; 
                }
            }
        }
        
        // draw the maze with a white background
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
                    getChildren().add(wall);
                }
            }
        }
        
        // Draw the walls vertically
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                if (verticalWalls[i][j]) {
                    double x1 = j * cellSize + padding;
                    double y1 = i * cellSize + padding;
                    double y2 = (i + 1) * cellSize + padding;
                    
                    Line wall = new Line(x1, y1, x1, y2);
                    wall.setStrokeWidth(wallThickness);
                    getChildren().add(wall);
                }
            }
        }
    }
}
