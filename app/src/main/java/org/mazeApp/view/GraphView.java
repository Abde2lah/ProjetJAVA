package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GraphView extends Pane {

    private double cellSize = 50;
    private double padding = 20;    
    
    public void draw(Graph graph) {
        // Erase the previous graph
        getChildren().clear();
        
        // Recup the data given by the user
        int rows = graph.getRows();
        int columns = graph.getColumns();
        
        // Draw the vertices
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            
            double x = col * cellSize + padding;
            double y = row * cellSize + padding;
            
            Circle vertex = new Circle(x, y, 5);
            vertex.setStyle("-fx-fill: white; -fx-stroke: black;");
            getChildren().add(vertex);
            
            // Draw the edges
            ArrayList<Edges> edges = graph.getGraphMaze().get(i);
            for (Edges edge : edges) {
                int destIndex = edge.getDestination();
                int destRow = destIndex / columns;
                int destCol = destIndex % columns;
                
                double destX = destCol * cellSize + padding;
                double destY = destRow * cellSize + padding;
                
                Line line = new Line(x, y, destX, destY);
                getChildren().add(line);
            }
        }
    }
}
