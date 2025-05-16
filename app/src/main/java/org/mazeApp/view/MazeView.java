package org.mazeApp.view;

import java.util.ArrayList;
import java.util.HashSet;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MazeView extends Pane {
    // Constantes d'affichage
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
    private final double padding = 2;
    private final double minWallThickness = 0.5;

    // État du labyrinthe
    private Graph currentGraph;
    private int rows, columns;
    private int startIndex = -1, endIndex = -1;
    private boolean selectingStart = true, selectingEnd = true, bothPointsPlaced = false;
    private double hoveredX = -1, hoveredY = -1;
    
    // Attributs pour l'édition de labyrinthe
    private GraphView associatedGraphView;
    private double hoveredWallX1 = -1, hoveredWallY1 = -1, hoveredWallX2 = -1, hoveredWallY2 = -1;
    private boolean wallHoverActive = false;

    /**
     * Constructeur avec graphe initial
     * @param graph le graphe représentant le labyrinthe
     */
    public MazeView(Graph graph) {
        this.currentGraph = graph;
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Constructeur avec graphe et vue graphe associée
     * @param graph le graphe représentant le labyrinthe
     * @param graphView la vue graphe associée
     */
    public MazeView(Graph graph, GraphView graphView) {
        this.currentGraph = graph;
        this.associatedGraphView = graphView;
        initializeView();
        setupEventHandlers();
    }
    
    /**
     * Définit la vue graphe associée pour la synchronisation
     */
    public void setAssociatedGraphView(GraphView graphView) {
        this.associatedGraphView = graphView;
    }

    // Initialise les propriétés visuelles de la vue
    private void initializeView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
    }

    // Configure les interactions souris : survol et clic
    private void setupEventHandlers() {
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;
            
            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();
            
            // Réinitialiser l'état du survol de mur
            wallHoverActive = false;
            
            // Vérifier si la souris est sur un mur ou sur une position où un mur peut être modifié
            checkWallHover(mouseX, mouseY, cellSize);
            
            // Code pour le survol des points de départ/arrivée
            if (!wallHoverActive && !bothPointsPlaced) {
                double gridX = mouseX - padding;
                double gridY = mouseY - padding;

                int col = (int) (gridX / cellSize);
                int row = (int) (gridY / cellSize);

                boolean isOnBorder = row >= 0 && row < rows && col >= 0 && col < columns &&
                                   (row == 0 || row == rows - 1 || col == 0 || col == columns - 1);

                if (isOnBorder) {
                    hoveredX = col * cellSize + padding + cellSize / 2;
                    hoveredY = row * cellSize + padding + cellSize / 2;
                } else {
                    hoveredX = -1;
                    hoveredY = -1;
                }
            } else {
                // Si au-dessus d'un mur ou si les deux points sont placés, désactiver le survol
                hoveredX = -1;
                hoveredY = -1;
            }
            
            // Changer le curseur selon le contexte
            if (wallHoverActive) {
                setCursor(javafx.scene.Cursor.HAND); // Curseur "main" pour indiquer une action possible
            } else if (hoveredX >= 0) {
                setCursor(javafx.scene.Cursor.CROSSHAIR); // Pour sélection de point de départ/arrivée
            } else {
                setCursor(javafx.scene.Cursor.DEFAULT); // Curseur normal dans les autres cas
            }
            
            draw();
        });

        // Clic souris : choisir point de départ/arrivée ou modifier un mur
        setOnMouseClicked(event -> {
            if (currentGraph == null) return;

            double cellSize = calculateCellSize();
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Détermine si le clic est proche d'un mur (lignes de grille)
            if (isNearWall(mouseX, mouseY, cellSize)) {
                System.out.println("Clic sur un mur détecté!");
                handleWallClick(mouseX, mouseY, cellSize);
                return;
            }

            // Gestion du début et de l'arrivée (code existant)
            if (hoveredX < 0 || bothPointsPlaced) return;
            
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

            if (!selectingStart && !selectingEnd) {
                bothPointsPlaced = true;
            }
        });
    }

    /**
     * Vérifie si les coordonnées de la souris sont près d'un mur ou emplacement de mur potentiel
     */
    private boolean isNearWall(double mouseX, double mouseY, double cellSize) {
        // Ajuste les coordonnées pour tenir compte du padding
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Vérifie la proximité des lignes verticales de la grille
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            // Calcule les coordonnées de la grille
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            // Vérifie si ce mur vertical est valide (pas les bords extérieurs du labyrinthe)
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                return true;  // Tous les murs intérieurs sont modifiables
            }
        }
        
        // Vérifie la proximité des lignes horizontales de la grille
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            // Calcule les coordonnées de la grille
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            
            // Vérifie si ce mur horizontal est valide (pas les bords extérieurs du labyrinthe)
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                return true;  // Tous les murs intérieurs sont modifiables
            }
        }
        
        return false;
    }

    /**
     * Vérifie si la souris est au-dessus d'un mur modifiable et configure les coordonnées du survol
     */
    private void checkWallHover(double mouseX, double mouseY, double cellSize) {
        // Coordonnées relatives à la grille
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Vérification des murs verticaux
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            // Si c'est un mur intérieur (pas les bordures)
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                // Coordonnées du mur pour l'affichage du survol
                hoveredWallX1 = gridCol * cellSize + padding;
                hoveredWallY1 = gridRow * cellSize + padding;
                hoveredWallX2 = gridCol * cellSize + padding;
                hoveredWallY2 = (gridRow + 1) * cellSize + padding;
                
                wallHoverActive = true;
                return;
            }
        }
        
        // Vérification des murs horizontaux
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            
            // Si c'est un mur intérieur (pas les bordures)
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                // Coordonnées du mur pour l'affichage du survol
                hoveredWallX1 = gridCol * cellSize + padding;
                hoveredWallY1 = gridRow * cellSize + padding;
                hoveredWallX2 = (gridCol + 1) * cellSize + padding;
                hoveredWallY2 = gridRow * cellSize + padding;
                
                wallHoverActive = true;
                return;
            }
        }
        
        // Aucun mur survolé
        wallHoverActive = false;
    }
    
    /**
     * Vérifie si deux cellules sont connectées
     */
    private boolean areConnected(int cell1, int cell2) {
        if (cell1 < 0 || cell1 >= currentGraph.getVertexNb() || 
            cell2 < 0 || cell2 >= currentGraph.getVertexNb()) {
            return false;
        }
        
        for (Edges edge : currentGraph.getGraphMaze().get(cell1)) {
            if (edge.getDestination() == cell2) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Gère le clic sur un mur
     */
    private void handleWallClick(double mouseX, double mouseY, double cellSize) {
        double gridX = mouseX - padding;
        double gridY = mouseY - padding;
        
        // Détection du mur vertical
        double distToVerticalGrid = gridX % cellSize;
        if (distToVerticalGrid < cellSize * 0.15 || distToVerticalGrid > cellSize * 0.85) {
            int gridCol = (int) Math.round(gridX / cellSize);
            int gridRow = (int) (gridY / cellSize);
            
            if (gridCol > 0 && gridCol < columns && gridRow >= 0 && gridRow < rows) {
                int cell1 = gridRow * columns + (gridCol - 1);
                int cell2 = gridRow * columns + gridCol;
                
                // Basculer la présence du mur
                toggleWall(cell1, cell2);
            }
        }
        
        // Détection du mur horizontal
        double distToHorizontalGrid = gridY % cellSize;
        if (distToHorizontalGrid < cellSize * 0.15 || distToHorizontalGrid > cellSize * 0.85) {
            int gridRow = (int) Math.round(gridY / cellSize);
            int gridCol = (int) (gridX / cellSize);
            
            if (gridRow > 0 && gridRow < rows && gridCol >= 0 && gridCol < columns) {
                int cell1 = (gridRow - 1) * columns + gridCol;
                int cell2 = gridRow * columns + gridCol;
                
                // Basculer la présence du mur
                toggleWall(cell1, cell2);
            }
        }
    }

    /**
     * Ajoute ou supprime un mur entre deux cellules
     */
    private void toggleWall(int cell1, int cell2) {
        if (cell1 < 0 || cell1 >= currentGraph.getVertexNb() || 
            cell2 < 0 || cell2 >= currentGraph.getVertexNb()) {
            return; // Cellules invalides
        }
        
        if (areConnected(cell1, cell2)) {
            // Si connectées, supprimer la connexion (ajouter un mur)
            for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(cell1))) {
                if (edge.getDestination() == cell2) {
                    currentGraph.getGraphMaze().get(cell1).remove(edge);
                }
            }
            
            for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(cell2))) {
                if (edge.getDestination() == cell1) {
                    currentGraph.getGraphMaze().get(cell2).remove(edge);
                }
            }
            
            System.out.println("Mur ajouté entre " + cell1 + " et " + cell2);
        } else {
            // Si non connectées, ajouter une connexion (supprimer un mur)
            currentGraph.getGraphMaze().get(cell1).add(new Edges(cell1, cell2));
            currentGraph.getGraphMaze().get(cell2).add(new Edges(cell2, cell1));
            
            System.out.println("Mur supprimé entre " + cell1 + " et " + cell2);
        }
        
        // Redessiner le labyrinthe
        draw();
        
        // Mettre à jour la vue du graphe si disponible
        if (associatedGraphView != null) {
            associatedGraphView.draw(currentGraph);
        }
    }
    
    /**
     * Dessine le labyrinthe actuel
     */
    public void draw() {
        getChildren().clear();
        if (currentGraph == null) return;

        this.rows = currentGraph.getRows();
        this.columns = currentGraph.getColumns();
        double cellSize = calculateCellSize();
        double wallThickness = Math.max(minWallThickness, cellSize * 0.1);

        boolean[][] horizontalWalls = new boolean[rows + 1][columns];
        boolean[][] verticalWalls = new boolean[rows][columns + 1];

        initializeWalls(horizontalWalls, verticalWalls);
        removeWallsBasedOnEdges(horizontalWalls, verticalWalls);

        drawBackground();
        drawWalls(horizontalWalls, verticalWalls, cellSize, wallThickness);
        drawSpecialPoints(cellSize);
    }

    // Initialise tous les murs à "présents"
    private void initializeWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
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
    }

    // Supprime les murs correspondant aux connexions du graphe
    private void removeWallsBasedOnEdges(boolean[][] horizontalWalls, boolean[][] verticalWalls) {
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int source = edge.getSource();
                int dest = edge.getDestination();

                int sourceX = source % columns;
                int sourceY = source / columns;
                int destX = dest % columns;
                int destY = dest / columns;

                if (sourceX == destX) {
                    int minY = Math.min(sourceY, destY);
                    horizontalWalls[minY + 1][sourceX] = false;
                } else if (sourceY == destY) {
                    int minX = Math.min(sourceX, destX);
                    verticalWalls[sourceY][minX + 1] = false;
                }
            }
        }
    }

    // Dessine le fond du labyrinthe (rectangle blanc)
    private void drawBackground() {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        Rectangle background = new Rectangle(padding, padding, availableWidth, availableHeight);
        background.setFill(Color.WHITE);
        getChildren().add(background);
    }

    // Dessine les murs du labyrinthe
    private void drawWalls(boolean[][] horizontalWalls, boolean[][] verticalWalls,
                           double cellSize, double wallThickness) {
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
        
        // Ajouter à la fin de la méthode pour dessiner le survol de mur
        if (wallHoverActive) {
            Line hoverLine = new Line(hoveredWallX1, hoveredWallY1, hoveredWallX2, hoveredWallY2);
            
            // Déterminer si c'est un mur horizontal ou vertical
            boolean isVertical = Math.abs(hoveredWallX1 - hoveredWallX2) < 0.01;
            
            int cell1, cell2;
            if (isVertical) {
                int gridCol = (int) Math.round((hoveredWallX1 - padding) / cellSize);
                int gridRow = (int) ((hoveredWallY1 - padding) / cellSize);
                cell1 = gridRow * columns + (gridCol - 1);
                cell2 = gridRow * columns + gridCol;
            } else {
                int gridRow = (int) Math.round((hoveredWallY1 - padding) / cellSize);
                int gridCol = (int) ((hoveredWallX1 - padding) / cellSize);
                cell1 = (gridRow - 1) * columns + gridCol;
                cell2 = gridRow * columns + gridCol;
            }
            
            boolean wallExists = !areConnected(cell1, cell2);
            
            hoverLine.setStrokeWidth(wallThickness * 2);  // Plus épais pour être visible
            hoverLine.setStroke(wallExists ? Color.RED : Color.GREEN);
            hoverLine.setOpacity(0.7);
            getChildren().add(hoverLine);
        }
    }

    // Dessine les points de départ, d'arrivée et le survol
    private void drawSpecialPoints(double cellSize) {
        double pointRadius = Math.max(0.5, cellSize / 4);

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

        if (hoveredX >= 0 && (selectingStart || selectingEnd)) {
            Circle hoverCircle = new Circle(hoveredX, hoveredY, pointRadius, Color.LIGHTBLUE);
            hoverCircle.setOpacity(0.5);
            getChildren().add(hoverCircle);
        }
    }

    // Calcule la taille d'une cellule en fonction de la grille et de l'espace
    private double calculateCellSize() {
        if (currentGraph == null) return 0;

        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        double widthBased = availableWidth / columns;
        double heightBased = availableHeight / rows;

        return Math.min(widthBased, heightBased);
    }

    /**
     * Trace un chemin sur le labyrinthe à partir d'une liste d'indices
     */
    public void drawPath(ArrayList<Integer> path) {
        if (path == null || path.isEmpty() || currentGraph == null) return;

        double cellSize = calculateCellSize();
        double pathThickness = Math.max(0.5, cellSize * 0.1);

        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);

            int row1 = current / columns;
            int col1 = current % columns;
            int row2 = next / columns;
            int col2 = next % columns;

            Line pathLine = new Line(
                col1 * cellSize + padding + cellSize / 2,
                row1 * cellSize + padding + cellSize / 2,
                col2 * cellSize + padding + cellSize / 2,
                row2 * cellSize + padding + cellSize / 2
            );
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(pathThickness);
            getChildren().add(pathLine);
        }
    }
    
    /**
     * Visualise les étapes de résolution d'un algorithme
     */
    public void visualiseStep(ArrayList<ArrayList<Integer>> steps) {
        if (steps.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return;
        }

        Timeline timeline = new Timeline();
        int delay = 100;

        // Stocke les segments et sommets visités
        ArrayList<int[]> visitedSegments = new ArrayList<>();
        HashSet<Integer> visitedVertices = new HashSet<>();

        for (int i = 0; i < steps.size(); i++) {
            final ArrayList<Integer> stepPath = steps.get(i);

            // Ajouter segments et sommets du chemin précédent
            if (i > 0) {
                ArrayList<Integer> previousPath = steps.get(i - 1);
                for (int j = 0; j < previousPath.size() - 1; j++) {
                    visitedSegments.add(new int[]{previousPath.get(j), previousPath.get(j + 1)});
                }
                visitedVertices.addAll(previousPath);
            }

            // Snapshots pour le lambda
            final ArrayList<int[]> segmentsSnapshot = new ArrayList<>(visitedSegments);
            final HashSet<Integer> verticesSnapshot = new HashSet<>(visitedVertices);

            KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                draw(); // Redessine le labyrinthe de base
                
                if (associatedGraphView != null) {
                    associatedGraphView.draw(currentGraph);
                }

                double cellSize = calculateCellSize();
                double pathThickness = Math.max(0.5, cellSize * 0.1);

                // 💚 Segments visités
                for (int[] segment : segmentsSnapshot) {
                    int from = segment[0], to = segment[1];

                    int row1 = from / columns;
                    int col1 = from % columns;
                    int row2 = to / columns;
                    int col2 = to % columns;

                    Line line = new Line(
                        col1 * cellSize + padding + cellSize / 2,
                        row1 * cellSize + padding + cellSize / 2,
                        col2 * cellSize + padding + cellSize / 2,
                        row2 * cellSize + padding + cellSize / 2
                    );
                    line.setStroke(Color.LIGHTGREEN);
                    line.setStrokeWidth(pathThickness);
                    getChildren().add(line);
                }

                // 💚 Sommets visités
                if (associatedGraphView != null) {
                    for (int index : verticesSnapshot) {
                        associatedGraphView.drawHighlightedVertices(index, currentGraph, Color.LIGHTGREEN);
                    }
                }

                // 🔴 Chemin rouge actuel
                drawPath(stepPath);
                if (associatedGraphView != null) {
                    associatedGraphView.drawHighlightedVertices(stepPath, currentGraph, Color.RED);
                }
            });

            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            System.out.println("Chemin trouvé de longueur " + steps.get(steps.size() - 1).size());
        });

        timeline.play();
    }

    /**
     * Réinitialise les points de départ et d'arrivée
     */
    public void resetStartEndPoints() {
        startIndex = -1;
        endIndex = -1;
        selectingStart = true;
        selectingEnd = true;
        bothPointsPlaced = false;
        draw();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}