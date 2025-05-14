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
    // Constantes et propriétés
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
    private final double padding = 2;
    private final double minWallThickness = 0.5;
    
    // État du labyrinthe
    private Graph currentGraph;
    private int rows, columns;
    private int startIndex = -1, endIndex = -1;
    private boolean selectingStart = true, selectingEnd = true;
    private double hoveredX = -1, hoveredY = -1;
    
    /**
     * Constructeur initialisé avec un graphe
     */
    public MazeView(Graph graph) {
        this.currentGraph = graph;
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Initialise les propriétés de base de la vue
     */
    private void initializeView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Gestion du survol de la souris
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;
            
            double cellSize = calculateCellSize();
            double mouseX = event.getX() - padding;
            double mouseY = event.getY() - padding;
            
            int col = (int) (mouseX / cellSize);
            int row = (int) (mouseY / cellSize);
            
            // Vérifier si le survol est sur la bordure
            boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns 
                              && (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);
                
            if (isOnBorder) {
                hoveredX = col * cellSize + padding + cellSize / 2;
                hoveredY = row * cellSize + padding + cellSize / 2;
            } else {
                hoveredX = -1;
                hoveredY = -1;
            }
            
            draw();
        });
        
        // Gestion des clics de souris
        setOnMouseClicked(event -> {
            if (currentGraph == null || hoveredX < 0) return;
            
            double cellSize = calculateCellSize();
            int col = (int) ((hoveredX - padding) / cellSize);
            int row = (int) ((hoveredY - padding) / cellSize);
            
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
        });
    }
    
    /**
     * Dessine le labyrinthe
     */
    public void draw() {
        getChildren().clear();
        if (currentGraph == null) return;
        
        // Initialisation des dimensions
        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();
        double cellSize = calculateCellSize();
        double wallThickness = Math.max(minWallThickness, cellSize * 0.1);
        
        // Préparer les tableaux des murs
        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];
        initializeWalls(horizontalWalls, verticalWalls);
        removeWallsBasedOnEdges(horizontalWalls, verticalWalls);
        
        // Fond blanc
        drawBackground();
        
        // Dessiner tous les murs
        drawWalls(horizontalWalls, verticalWalls, cellSize, wallThickness);
        
        // Dessiner les points spéciaux
        drawSpecialPoints(cellSize);
    }
    
    /**
     * Initialise les murs à true (tous présents)
     */
    private void initializeWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        // Initialiser tous les murs horizontaux
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                horizontalWalls[i][j] = true;
            }
        }
        
        // Initialiser tous les murs verticaux
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                verticalWalls[i][j] = true;
            }
        }
    }
    
    /**
     * Supprime les murs en fonction des arêtes du graphe
     */
    private void removeWallsBasedOnEdges(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int source = edge.getSource();
                int dest = edge.getDestination();
                
                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;
                
                // Supprimer le mur entre les cellules connectées
                if (sourceX == destX) { // Même colonne (passage vertical)
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) { // Même ligne (passage horizontal)
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX + 1] = false;
                }
            }
        }
    }
    
    /**
     * Dessine le fond blanc du labyrinthe
     */
    private void drawBackground() {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);
        
        Rectangle background = new Rectangle(padding, padding, availableWidth, availableHeight);
        background.setFill(Color.WHITE);
        getChildren().add(background);
    }
    
    /**
     * Dessine tous les murs horizontaux et verticaux
     */
    private void drawWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls, 
                          double cellSize, double wallThickness) {
        // Murs horizontaux
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (horizontalWalls[i][j]) {
                    Line wall = new Line(
                        j * cellSize + padding, 
                        i * cellSize + padding,
                        (j + 1) * cellSize + padding, 
                        i * cellSize + padding
                    );
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }
        
        // Murs verticaux
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                if (verticalWalls[i][j]) {
                    Line wall = new Line(
                        j * cellSize + padding, 
                        i * cellSize + padding,
                        j * cellSize + padding, 
                        (i + 1) * cellSize + padding
                    );
                    wall.setStrokeWidth(wallThickness);
                    wall.setStroke(Color.BLACK);
                    getChildren().add(wall);
                }
            }
        }
    }
    
    /**
     * Dessine les points spéciaux (départ, arrivée, survol)
     */
    private void drawSpecialPoints(double cellSize) {
        double pointRadius = Math.max(0.5, cellSize / 4);
        
        // Point de départ (vert)
        if (startIndex >= 0) {
            int row = startIndex / columns;
            int col = startIndex % columns;
            Circle startCircle = new Circle(
                col * cellSize + padding + cellSize / 2,
                row * cellSize + padding + cellSize / 2, 
                pointRadius, Color.GREEN
            );
            getChildren().add(startCircle);
        }
        
        // Point d'arrivée (rouge)
        if (endIndex >= 0) {
            int row = endIndex / columns;
            int col = endIndex % columns;
            Circle endCircle = new Circle(
                col * cellSize + padding + cellSize / 2,
                row * cellSize + padding + cellSize / 2, 
                pointRadius, Color.RED
            );
            getChildren().add(endCircle);
        }
        
        // Cercle de survol (bleu clair)
        if (hoveredX >= 0 && (selectingStart || selectingEnd)) {
            Circle hoverCircle = new Circle(hoveredX, hoveredY, pointRadius, Color.LIGHTBLUE);
            hoverCircle.setOpacity(0.5);
            getChildren().add(hoverCircle);
        }
    }
    
    /**
     * Calcule la taille des cellules pour s'adapter à l'espace fixe
     */
    private double calculateCellSize() {
        if (currentGraph == null) return 0;
        
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);
        
        double widthBasedSize = availableWidth / columns;
        double heightBasedSize = availableHeight / rows;
        
        return Math.min(widthBasedSize, heightBasedSize);
    }
    
    /**
     * Dessine un chemin dans le labyrinthe
     */
    public void drawPath(ArrayList<Integer> path) {
        if (path == null || path.isEmpty() || currentGraph == null) return;
        
        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);
        
        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);
            
            int currentRow = current / columns;
            int currentCol = current % columns;
            int nextRow = next / columns;
            int nextCol = next % columns;
            
            Line pathLine = new Line(
                currentCol * cellSize + padding + cellSize / 2,
                currentRow * cellSize + padding + cellSize / 2,
                nextCol * cellSize + padding + cellSize / 2,
                nextRow * cellSize + padding + cellSize / 2
            );
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(pathThickness);
            getChildren().add(pathLine);
        }
    }
    
    /**
     * Met à jour le graphe et redessine le labyrinthe
     */
    public void draw(Graph graph) {
        this.currentGraph = graph;
        resetStartEndPoints();
    }
    
    /**
     * Réinitialise les points de départ et d'arrivée
     */
    public void resetStartEndPoints() {
        startIndex = -1;
        endIndex = -1;
        selectingStart = true;
        selectingEnd = true;
        draw();
    }
    
    public void clear() {
        getChildren().clear();
    }
    
    public int getStartIndex() {
        return startIndex;
    }
    
    public int getEndIndex() {
        return endIndex;
    }
}