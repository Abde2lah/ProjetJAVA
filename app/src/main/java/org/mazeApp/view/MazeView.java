package org.mazeApp.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.mazeApp.model.Graph;
import org.mazeApp.model.Edges;
import java.util.ArrayList;

public class MazeView extends Pane {
    private double cellSize = 40; // Cellules plus grandes pour une meilleure visibilité
    private double padding = 20;
    private double wallThickness = 2.5; // Murs plus épais pour meilleure visibilité

    public void draw(Graph graph) {
        // Effacer tous les éléments existants avant de redessiner
        getChildren().clear();
        
        // Récupérer les dimensions réelles du labyrinthe
        int rows = graph.getRows();
        int columns = graph.getColumns();
        
        // Créer une structure pour savoir quels murs doivent être dessinés
        boolean[][] horizontalWalls = new boolean[rows+1][columns]; // Murs horizontaux
        boolean[][] verticalWalls = new boolean[rows][columns+1];   // Murs verticaux
        
        // Par défaut, tous les murs sont présents
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
        
        // Supprimer les murs entre les cellules connectées
        for (int i = 0; i < graph.getGraphMaze().size(); i++) {
            ArrayList<Edges> edges = graph.getGraphMaze().get(i);
            for (Edges edge : edges) {
                int source = edge.getSource();
                int dest = edge.getDestination();
                
                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;
                
                // Retirer les murs entre les cellules connectées
                if (sourceX == destX) { // Même colonne - passage vertical
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY+1][sourceX] = false; // Enlever le mur horizontal entre les cellules
                } else if (sourceY == destY) { // Même ligne - passage horizontal
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX+1] = false; // Enlever le mur vertical entre les cellules
                }
            }
        }
        
        // Dessiner le fond blanc pour toutes les cellules
        Rectangle background = new Rectangle(padding, padding, columns * cellSize, rows * cellSize);
        background.setFill(Color.WHITE);
        getChildren().add(background);
        
        // Dessiner les murs horizontaux
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
        
        // Dessiner les murs verticaux
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
