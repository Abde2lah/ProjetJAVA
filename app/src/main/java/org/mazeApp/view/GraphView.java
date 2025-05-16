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
     * Constructeur : initialise les dimensions fixes de la vue
     */
    public GraphView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }

    /**
     * Dessine le graphe dans le panneau.
     * 
     * @param graph Le graphe à dessiner
     */
    public void draw(Graph graph) {
        getChildren().clear();
        if (graph == null || graph.getGraphMaze() == null) {
            System.out.println("Erreur : Le graphe est null ou invalide.");
            return;
        }

        // Récupération des dimensions du graphe
        int rows = graph.getRows();
        int columns = graph.getColumns();
        int totalVertices = rows * columns;

        // Calculs pour adapter les dimensions
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
        double lineWidth = calculateLineWidth(cellSize);

        drawBackground(); // fond blanc
        drawEdges(graph, columns, cellSize, lineWidth); // d'abord les arêtes
        drawVertices(totalVertices, columns, cellSize, vertexRadius); // puis les sommets
    }

    /**
     * Dessine le fond blanc du graphe avec un léger contour
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
     * Dessine les arêtes (connexions) du graphe.
     * 
     * @param graph Le graphe à afficher
     * @param columns Nombre de colonnes de la grille
     * @param cellSize Taille d’une cellule
     * @param lineWidth Épaisseur des lignes
     */
    private void drawEdges(Graph graph, int columns, double cellSize, double lineWidth) {
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            int row = i / columns;
            int col = i % columns;

            double x = col * cellSize + padding;
            double y = row * cellSize + padding;

            for (Edges edge : graph.getGraphMaze().get(i)) {
                int destIndex = edge.getDestination();
                if (destIndex < i) continue; // éviter les doublons d’arêtes

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
     * Dessine les sommets (points) du graphe.
     * 
     * @param totalVertices Nombre total de sommets
     * @param columns Nombre de colonnes
     * @param cellSize Taille de cellule
     * @param vertexRadius Rayon du cercle représentant un sommet
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
     * Calcule la taille optimale d'une cellule dans la grille.
     * 
     * @param rows Nombre de lignes
     * @param columns Nombre de colonnes
     * @return Taille de cellule
     */
    private double calculateCellSize(int rows, int columns) {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        double widthBasedSize = availableWidth / columns;
        double heightBasedSize = availableHeight / rows;

        return Math.min(widthBasedSize, heightBasedSize);
    }

    /**
     * Calcule un rayon adapté pour les sommets en fonction de la cellule.
     * 
     * @param cellSize Taille d’une cellule
     * @return Rayon du sommet
     */
    private double calculateVertexRadius(double cellSize) {
        double radius = cellSize * 0.25;
        return Math.max(minVertexRadius, Math.min(radius, maxVertexRadius));
    }

    /**
     * Calcule l'épaisseur des lignes en fonction de la taille des cellules.
     * 
     * @param cellSize Taille d’une cellule
     * @return Épaisseur des lignes
     */
    private double calculateLineWidth(double cellSize) {
        double width = cellSize * 0.05;
        return Math.max(0.5, Math.min(width, 2.0));
    }

    public void highlightVertex(int index, Graph model) {
        // Redessine le graphe avec le sommet "index" en surbrillance
        draw(model); // Redessine de base
        int rows = model.getRows();
        int cols = model.getColumns();
        double cellSize = calculateCellSize(rows, cols);
        double radius = calculateVertexRadius(cellSize);

        int row = index / cols;
        int col = index % cols;

        double x = col * cellSize + padding;
        double y = row * cellSize + padding;

        javafx.scene.shape.Circle highlight = new javafx.scene.shape.Circle(x, y, radius + 2);
        highlight.setFill(Color.LIGHTGREEN);
        getChildren().add(highlight);
    }

    public void drawHighlightedVertices(ArrayList<Integer> indices, Graph model, Color color) {
        if (indices == null || indices.isEmpty()) return;

        int rows = model.getRows();
        int columns = model.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        double radius = calculateVertexRadius(cellSize);

        for (int index : indices) {
            int row = index / columns;
            int col = index % columns;

            double x = col * cellSize + padding;
            double y = row * cellSize + padding;

            javafx.scene.shape.Circle highlight = new javafx.scene.shape.Circle(x, y, radius + 2);
            highlight.setFill(color);
            getChildren().add(highlight);
        }
    }


    public void drawHighlightedVertices(int index, Graph model, Color color) {

        int rows = model.getRows();
        int columns = model.getColumns();

        int row = index / columns;
        int col = index % columns;
        double cellSize = calculateCellSize(rows, columns);
        double radius = calculateVertexRadius(cellSize);
    
        double x = col * cellSize + padding;
        double y = row * cellSize + padding;
    
        javafx.scene.shape.Circle highlight = new javafx.scene.shape.Circle(x, y, radius + 2);
        highlight.setFill(color);
        getChildren().add(highlight);
    }

    

}
