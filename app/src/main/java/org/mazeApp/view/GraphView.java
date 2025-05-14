package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GraphView extends Pane {

    private double cellSize = 50;
    private double padding = 20; 
    
    /**
     * Draw the graph in the pane.
     * 
     * @param graph The graph to be drawn.
     */
    public void draw(Graph graph) {
        // Clear previous graph
        getChildren().clear();
        
        // Verify if the graph is null
        if (graph == null || graph.getGraphMaze() == null) {
            System.out.println("Erreur : Le graphe est null ou invalide.");
            return;
        }

        // Récupthe graph dimensions
        int rows = graph.getRows();
        int columns = graph.getColumns();
        
        // Draw the vertices
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            
            double x = col * cellSize + padding;
            double y = row * cellSize + padding;
            
            // Create vertex
            Circle vertex = new Circle(x, y, 5);
            vertex.setFill(Color.WHITE);
            vertex.setStroke(Color.BLACK);
            getChildren().add(vertex);
            
            // Draw the edges
            ArrayList<Edges> edges = graph.getGraphMaze().get(i);
            for (Edges edge : edges) {
                int destIndex = edge.getDestination();
                int destRow = destIndex / columns;
                int destCol = destIndex % columns;
                
                double destX = destCol * cellSize + padding;
                double destY = destRow * cellSize + padding;
                
                // Edge creation
                Line line = new Line(x, y, destX, destY);
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(1.5);
                getChildren().add(line);
            }
        }
    }
}