package org.mazeApp.view;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import org.mazeApp.model.Graph;
import org.mazeApp.model.Edges;
import java.util.ArrayList;

public class GraphView extends Pane {

    private double cellSize = 50;
    private double padding = 20;    
    
    public void draw(Graph graph) {
        // Effacer tous les éléments existants avant de redessiner
        getChildren().clear();
        
        // Récupérer les dimensions réelles du labyrinthe
        int rows = graph.getRows();
        int columns = graph.getColumns();
        
        // Dessiner les sommets
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            
            double x = col * cellSize + padding;
            double y = row * cellSize + padding;
            
            Circle vertex = new Circle(x, y, 5);
            vertex.setStyle("-fx-fill: white; -fx-stroke: black;");
            getChildren().add(vertex);
            
            // Dessiner les arêtes
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
