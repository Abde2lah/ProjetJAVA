package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GraphView extends Pane {
    // Constantes et propriétés
    private final double FIXED_WIDTH = 300;
    private final double FIXED_HEIGHT = 300;
    private final double padding = 20;
    private final double minVertexRadius = 0.5;
    private final double maxVertexRadius = 5;
    
    // Variables pour l'édition du graphe
    private Graph currentGraph;
    private MazeView associatedMazeView;
    private int hoveredVertexIndex = -1;
    private int selectedVertexIndex = -1;
    private int[] hoveredEdge = null;
    private int[] potentialEdge = null; // Pour afficher les arêtes potentielles
    private boolean edgeCreationMode = false;
    private double edgeStartX, edgeStartY;
    private final double HOVER_THRESHOLD = 10.0;
    
    // Couleurs pour l'interface
    private final Color EDGE_HOVER_COLOR = Color.RED;
    private final Color VERTEX_HOVER_COLOR = Color.LIGHTBLUE;
    private final Color POTENTIAL_EDGE_COLOR = Color.GREEN;
    

    /**
     * Constructeur : initialise les dimensions fixes de la vue
     */
    public GraphView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
        setupEventHandlers();
    }

    /**
     * Définit la vue du labyrinthe associée pour la synchronisation
     */
    public void setAssociatedMazeView(MazeView mazeView) {
        this.associatedMazeView = mazeView;
    }

    /**
     * Configure les gestionnaires d'événements pour l'interaction
     */
    private void setupEventHandlers() {
        // Gestion du survol
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;
            
            double mouseX = event.getX();
            double mouseY = event.getY();
            
            // Mode création d'arête
            if (edgeCreationMode) {
                // Le code reste inchangé pour le mode création d'arête
                // ...
                return;
            }
            
            // Mode normal - réinitialiser l'état
            boolean wasHoveringVertex = hoveredVertexIndex != -1;
            boolean wasHoveringEdge = hoveredEdge != null;
            boolean hadPotentialEdge = potentialEdge != null;
            
            hoveredVertexIndex = -1;
            hoveredEdge = null;
            potentialEdge = null;
            
            // Vérifier les survols dans cet ordre: arêtes, arêtes potentielles, puis sommets
            
            // 1. Vérifier survol sur arête existante
            checkEdgeHover(mouseX, mouseY);
            
            // 2. Si aucune arête n'est survolée, vérifier les arêtes potentielles
            if (hoveredEdge == null) {
                findPotentialEdge(mouseX, mouseY);
            }
            
            // 3. Si aucune arête (existante ou potentielle) n'est survolée, vérifier les sommets
            if (hoveredEdge == null && potentialEdge == null) {
                checkVertexHover(mouseX, mouseY);
            }
            
            // Mise à jour de l'interface selon ce qui est survolé
            if (hoveredEdge != null) {
                // Survol d'une arête existante
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawEdgeHover(hoveredEdge[0], hoveredEdge[1], EDGE_HOVER_COLOR);
            } else if (potentialEdge != null) {
                // Survol d'une arête potentielle
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawPotentialEdge(potentialEdge[0], potentialEdge[1], POTENTIAL_EDGE_COLOR);
            } else if (hoveredVertexIndex != -1) {
                // Survol d'un sommet
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawVertexHover(hoveredVertexIndex, VERTEX_HOVER_COLOR);
            } else {
                // Aucun élément survolé
                setCursor(javafx.scene.Cursor.DEFAULT);
                if (wasHoveringVertex || wasHoveringEdge || hadPotentialEdge) {
                    draw(currentGraph);
                }
            }
        });
        
        // Gestion des clics
        setOnMouseClicked(event -> {
            if (currentGraph == null) return;
            
            double mouseX = event.getX();
            double mouseY = event.getY();
            if (edgeCreationMode) {
                return;
            }
            
            // Clic sur une arête existante - supprimer l'arête
            if (hoveredEdge != null) {
                removeEdge(hoveredEdge[0], hoveredEdge[1]);
                hoveredEdge = null;
                draw(currentGraph);
                return;
            }
            
            // Clic sur une arête potentielle - ajouter l'arête
            if (potentialEdge != null) {
                addEdge(potentialEdge[0], potentialEdge[1]);
                potentialEdge = null;
                draw(currentGraph);
                return;
            }
            
            // Clic sur un sommet - débuter création d'arête si Shift enfoncé
            if (hoveredVertexIndex != -1) {
                if (event.isShiftDown()) {
                    selectedVertexIndex = hoveredVertexIndex;
                    edgeCreationMode = true;
                    
                    // Coordonnées du sommet pour la ligne temporaire
                    int rows = currentGraph.getRows();
                    int columns = currentGraph.getColumns();
                    double cellSize = calculateCellSize(rows, columns);
                    int row = selectedVertexIndex / columns;
                    int col = selectedVertexIndex % columns;
                    edgeStartX = col * cellSize + padding;
                    edgeStartY = row * cellSize + padding;
                    
                    setCursor(javafx.scene.Cursor.CROSSHAIR);
                    System.out.println("Début création d'arête depuis sommet " + selectedVertexIndex);
                } else {
                    // Clic simple sur un sommet (peut être utilisé pour d'autres actions)
                    System.out.println("Sommet sélectionné : " + hoveredVertexIndex);
                }
            }
        });
    }
    
    /**
     * Cherche une arête potentielle à créer lorsque la souris est entre deux sommets
     */
    private void findPotentialEdge(double mouseX, double mouseY) {
        if (currentGraph == null) return;
        
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        
        // Rechercher toutes les paires de sommets adjacents qui n'ont pas d'arête entre eux
        for (int i = 0; i < currentGraph.getVertexNb(); i++) {
            int row1 = i / columns;
            int col1 = i % columns;
            double x1 = col1 * cellSize + padding;
            double y1 = row1 * cellSize + padding;
            
            // Vérifier uniquement les voisins directs (haut, bas, gauche, droite)
            int[] directions = {-columns, -1, 1, columns}; // haut, gauche, droite, bas
            int[] neighborIndices = new int[4];
            
            for (int d = 0; d < 4; d++) {
                int neighborIndex = i + directions[d];
                
                // Vérifier que le voisin est valide
                if (neighborIndex < 0 || neighborIndex >= currentGraph.getVertexNb()) {
                    neighborIndices[d] = -1;
                    continue;
                }
                
                // Éviter les arêtes qui traversent les bords de la grille
                if (directions[d] == -1 && i % columns == 0) {
                    neighborIndices[d] = -1;
                    continue;
                }
                if (directions[d] == 1 && i % columns == columns - 1) {
                    neighborIndices[d] = -1;
                    continue;
                }
                
                neighborIndices[d] = neighborIndex;
            }
            
            // Vérifier chaque voisin valide
            for (int j : neighborIndices) {
                if (j == -1) continue;
                
                // Vérifier si une arête existe déjà
                boolean edgeExists = false;
                for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                    if (edge.getDestination() == j) {
                        edgeExists = true;
                        break;
                    }
                }
                
                // Si pas d'arête, vérifier si la souris est proche de la ligne entre ces sommets
                if (!edgeExists) {
                    int row2 = j / columns;
                    int col2 = j % columns;
                    double x2 = col2 * cellSize + padding;
                    double y2 = row2 * cellSize + padding;
                    
                    // Calculer la distance du point (mouseX, mouseY) à la ligne
                    double distance = distancePointToLine(mouseX, mouseY, x1, y1, x2, y2);
                    
                    if (distance <= HOVER_THRESHOLD) {
                        potentialEdge = new int[] {i, j};
                        return; // On a trouvé une arête potentielle, on s'arrête
                    }
                }
            }
        }
    }
    
    /**
     * Vérifie si la souris est au-dessus d'un sommet
     */
    private boolean checkVertexHover(double mouseX, double mouseY) {
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
        
        // Seuil adaptatif
        double threshold = Math.max(HOVER_THRESHOLD, vertexRadius * 2);
        
        for (int i = 0; i < currentGraph.getVertexNb(); i++) {
            int row = i / columns;
            int col = i % columns;
            
            double vertexX = col * cellSize + padding;
            double vertexY = row * cellSize + padding;
            
            double distance = Math.sqrt(Math.pow(mouseX - vertexX, 2) + Math.pow(mouseY - vertexY, 2));
            
            if (distance <= threshold) {
                hoveredVertexIndex = i;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si la souris est au-dessus d'une arête
     */
    private boolean checkEdgeHover(double mouseX, double mouseY) {
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        
        for (int i = 0; i < currentGraph.getGraphMaze().size(); i++) {
            int row1 = i / columns;
            int col1 = i % columns;
            double x1 = col1 * cellSize + padding;
            double y1 = row1 * cellSize + padding;
            
            for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                int j = edge.getDestination();
                // Éviter les doublons
                if (i >= j) continue;
                
                int row2 = j / columns;
                int col2 = j % columns;
                double x2 = col2 * cellSize + padding;
                double y2 = row2 * cellSize + padding;
                
                // Distance point-ligne
                double distance = distancePointToLine(mouseX, mouseY, x1, y1, x2, y2);
                
                if (distance <= HOVER_THRESHOLD) {
                    hoveredEdge = new int[] {i, j};
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Calcule la distance entre un point et une ligne
     */
    private double distancePointToLine(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        
        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        
        if (len_sq != 0) param = dot / len_sq;
        
        double xx, yy;
        
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        
        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Dessine un effet de survol sur un sommet
     */
    private void drawVertexHover(int index, Color color) {
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        double radius = calculateVertexRadius(cellSize);
        
        int row = index / columns;
        int col = index % columns;
        
        double x = col * cellSize + padding;
        double y = row * cellSize + padding;
        
        Circle hoverCircle = new Circle(x, y, radius * 1.8);
        hoverCircle.setFill(color);
        hoverCircle.setOpacity(0.5);
        getChildren().add(hoverCircle);
    }
    
    /**
     * Dessine un effet de survol sur une arête
     */
    private void drawEdgeHover(int source, int dest, Color color) {
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        
        int row1 = source / columns;
        int col1 = source % columns;
        double x1 = col1 * cellSize + padding;
        double y1 = row1 * cellSize + padding;
        
        int row2 = dest / columns;
        int col2 = dest % columns;
        double x2 = col2 * cellSize + padding;
        double y2 = row2 * cellSize + padding;
        
        Line hoverLine = new Line(x1, y1, x2, y2);
        hoverLine.setStroke(color);
        hoverLine.setStrokeWidth(calculateLineWidth(cellSize) * 2);
        getChildren().add(hoverLine);
    }
    
    /**
     * Ajoute une arête entre deux sommets
     */
    private void addEdge(int source, int destination) {
        if (source == destination) return;
        
        if (source < 0 || destination < 0 || 
            source >= currentGraph.getVertexNb() || 
            destination >= currentGraph.getVertexNb()) {
            System.err.println("Erreur: indices de sommets invalides: " + source + " -> " + destination);
            return;
        }
        
        // Vérifier si l'arête existe déjà
        boolean edgeExists = false;
        for (Edges edge : currentGraph.getGraphMaze().get(source)) {
            if (edge.getDestination() == destination) {
                edgeExists = true;
                break;
            }
        }
        
        if (!edgeExists) {
            try {
                // Ajouter directement au graphe de façon bidirectionnelle (sans appel à Graph.addEdge())
                currentGraph.getGraphMaze().get(source).add(new Edges(source, destination));
                currentGraph.getGraphMaze().get(destination).add(new Edges(destination, source));
                
                System.out.println("Arête ajoutée avec succès entre " + source + " et " + destination);
                
                // Redessiner le graphe
                draw(currentGraph);
                
                // Mettre à jour le labyrinthe
                if (associatedMazeView != null) {
                    associatedMazeView.draw();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ajout d'arête: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("L'arête existe déjà entre " + source + " et " + destination);
        }
    }
    
    /**
     * Supprime une arête entre deux sommets
     */
    private void removeEdge(int source, int destination) {
        if (source == destination) return;
        
        boolean removed = false;
        
        // Supprimer dans les deux sens
        for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(source))) {
            if (edge.getDestination() == destination) {
                currentGraph.getGraphMaze().get(source).remove(edge);
                removed = true;
            }
        }
        
        for (Edges edge : new ArrayList<>(currentGraph.getGraphMaze().get(destination))) {
            if (edge.getDestination() == source) {
                currentGraph.getGraphMaze().get(destination).remove(edge);
                removed = true;
            }
        }
        
        if (removed) {
            System.out.println("Arête supprimée entre " + source + " et " + destination);
            
            // Redessiner le graphe
            draw(currentGraph);
            
            // Mettre à jour le labyrinthe
            if (associatedMazeView != null) {
                associatedMazeView.draw();
            }
        }
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
        
        // Stocker le graphe courant
        this.currentGraph = graph;

        // Récupération des dimensions du graphe
        int rows = graph.getRows();
        int columns = graph.getColumns();
        int totalVertices = rows * columns;

        // Calculs pour adapter les dimensions
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
        double lineWidth = calculateLineWidth(cellSize);

        drawEdges(graph, columns, cellSize, lineWidth); // d'abord les arêtes
        drawVertices(totalVertices, columns, cellSize, vertexRadius); // puis les sommets
    }

    /**
     * Dessine le fond blanc du graphe avec un léger contour
     */

    /**
     * Dessine les arêtes (connexions) du graphe.
     * 
     * @param graph Le graphe à afficher
     * @param columns Nombre de colonnes de la grille
     * @param cellSize Taille d'une cellule
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
                if (destIndex < i) continue; // éviter les doublons d'arêtes

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
     * @param cellSize Taille d'une cellule
     * @return Rayon du sommet
     */
    private double calculateVertexRadius(double cellSize) {
        double radius = cellSize * 0.25;
        return Math.max(minVertexRadius, Math.min(radius, maxVertexRadius));
    }

    /**
     * Calcule l'épaisseur des lignes en fonction de la taille des cellules.
     * 
     * @param cellSize Taille d'une cellule
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

    /**
     * Dessine une arête potentielle (ligne verte) entre deux sommets.
     */
    private void drawPotentialEdge(int source, int dest, Color color) {
        if (currentGraph == null) return;
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);

        int row1 = source / columns;
        int col1 = source % columns;
        double x1 = col1 * cellSize + padding;
        double y1 = row1 * cellSize + padding;

        int row2 = dest / columns;
        int col2 = dest % columns;
        double x2 = col2 * cellSize + padding;
        double y2 = row2 * cellSize + padding;

        Line potentialLine = new Line(x1, y1, x2, y2);
        potentialLine.setStroke(color);
        potentialLine.setStrokeWidth(calculateLineWidth(cellSize) * 2);
        getChildren().add(potentialLine);
    }
}