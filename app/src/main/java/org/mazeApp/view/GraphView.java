package org.mazeApp.view;

import java.util.ArrayList;
import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GraphView extends Pane {
    // Constantes et propriétés
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
    private final double padding = 20;
    private final double minVertexRadius = 0.5;
    private final double maxVertexRadius = 5;
    
    /**
     * Constructeur qui initialise la vue
     */
    public GraphView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }
    
    /**
     * Dessine le graphe
     */
    public void draw(Graph graph) {
        getChildren().clear();
        if (graph == null || graph.getGraphMaze() == null) {
            System.out.println("Erreur : Le graphe est null ou invalide.");
            return;
        }
        
        // Récupérer les dimensions du graphe
        int rows = graph.getRows();
        int columns = graph.getColumns();
        int totalVertices = rows * columns;
        
        // Calculer les dimensions adaptatives
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
        double lineWidth = calculateLineWidth(cellSize);
        
        // Fond blanc
        drawBackground();
        
        // Dessiner les arêtes puis les sommets
        drawEdges(graph, columns, cellSize, lineWidth);
        drawVertices(totalVertices, columns, cellSize, vertexRadius);
    }
    
    /**
     * Dessine le fond blanc du graphe
     */
    private void drawBackground() {
        Rectangle background = new Rectangle(
            padding, padding,
            FIXED_WIDTH - (2 * padding), 
            FIXED_HEIGHT - (2 * padding)
        );
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);
        getChildren().add(background);
    }
    
    /**
     * Dessine les arêtes du graphe
     */
    private void drawEdges(Graph graph, int columns, double cellSize, double lineWidth) {
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            
            double x = col * cellSize + padding;
            double y = row * cellSize + padding;
            
            for (Edges edge : graph.getGraphMaze().get(i)) {
                int destIndex = edge.getDestination();
                int destRow = destIndex / columns;
                int destCol = destIndex % columns;
                
                double destX = destCol * cellSize + padding;
                double destY = destRow * cellSize + padding;
                
                Line line = new Line(x, y, destX, destY);
                line.setStroke(Color.GRAY);
                line.setStrokeWidth(lineWidth);
                getChildren().add(line);
            }
        }
    }
    
    /**
     * Dessine les sommets du graphe
     */
    private void drawVertices(int totalVertices, int columns, double cellSize, double vertexRadius) {
        for (int i = 0; i < totalVertices; i++) {
            int row = i / columns;
            int col = i % columns;
            
            double x = col * cellSize + padding;
            double y = row * cellSize + padding;
            
            Circle vertex = new Circle(x, y, vertexRadius);
            vertex.setFill(Color.WHITE);
            vertex.setStroke(Color.BLACK);
            vertex.setStrokeWidth(Math.max(0.3, vertexRadius * 0.2));
            getChildren().add(vertex);
        }
    }
    
    /**
     * Calcule la taille des cellules pour s'adapter à l'espace fixe
     */
    private double calculateCellSize(int rows, int columns) {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);
        
        double widthBasedSize = availableWidth / columns;
        double heightBasedSize = availableHeight / rows;
        
        return Math.min(widthBasedSize, heightBasedSize);
    }
    
    /**
     * Calcule le rayon optimal des sommets
     */
    private double calculateVertexRadius(double cellSize) {
        double radius = cellSize * 0.25;
        return Math.max(minVertexRadius, Math.min(radius, maxVertexRadius));
    }
    
    /**
     * Calcule l'épaisseur optimale des lignes
     */
    private double calculateLineWidth(double cellSize) {
        double width = cellSize * 0.05;
        return Math.max(0.5, Math.min(width, 2.0));
    }
}