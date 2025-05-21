package org.mazeApp.view;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.EditingView.GraphEditor;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * GraphView is a JavaFX component that visually represents a graph structure.
 * <p>
 * This class displays vertices and edges in a grid layout, and allows interaction
 * such as edge creation and deletion through mouse events.
 * It synchronizes with the {@link MazeView} to reflect graph updates.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class GraphView extends Pane {
    // Constants and properties
    private final double FIXED_WIDTH = 250;
    private final double FIXED_HEIGHT = 250;
    private final double padding = 20;
    private final double minVertexRadius = 0.5;
    private final double maxVertexRadius = 5;
    
    // Variables to edit the graph
    private Graph currentGraph;
    private GraphEditor graphEditor;
    private MazeView associatedMazeView;
    private int hoveredVertexIndex = -1;
    private int selectedVertexIndex = -1;
    private int[] hoveredEdge = null;
    private int[] potentialEdge = null; 
    private boolean edgeCreationMode = false;
    private double edgeStartX, edgeStartY;
    private final double HOVER_THRESHOLD = 10.0;
    
    // Colors for the UI
    private final Color EDGE_HOVER_COLOR = Color.RED;
    private final Color VERTEX_HOVER_COLOR = Color.LIGHTBLUE;
    private final Color POTENTIAL_EDGE_COLOR = Color.GREEN;
    /**
     * Constructs the GraphView and sets up default dimensions and event handlers.
     */
    public GraphView() {
        setPrefSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMinSize(FIXED_WIDTH, FIXED_HEIGHT);
        setMaxSize(FIXED_WIDTH, FIXED_HEIGHT);
        graphEditor = new GraphEditor();
        setupEventHandlers();
    }

    /**
     * Sets the associated MazeView to allow for synchronized updates when the graph is edited.
     *
     * @param mazeView the MazeView to synchronize with
     */
    public void setAssociatedMazeView(MazeView mazeView) {
        this.associatedMazeView = mazeView;
    }

    /**
     * Setup the events gestionary in case if happened
     */
    private void setupEventHandlers() {
        // hover gestion with the mouse
        setOnMouseMoved(event -> {
            if (currentGraph == null) return;
            
            double mouseX = event.getX();
            double mouseY = event.getY();
            
            if (edgeCreationMode) {
                return;
            }
            
            boolean wasHoveringVertex = hoveredVertexIndex != -1;
            boolean wasHoveringEdge = hoveredEdge != null;
            boolean hadPotentialEdge = potentialEdge != null;
            
            hoveredVertexIndex = -1;
            hoveredEdge = null;
            potentialEdge = null;
            checkEdgeHover(mouseX, mouseY);
            
            if (hoveredEdge == null) {
                findPotentialEdge(mouseX, mouseY);
            }
            
            if (hoveredEdge == null && potentialEdge == null) {
                checkVertexHover(mouseX, mouseY);
            }
            
            if (hoveredEdge != null) {
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawEdgeHover(hoveredEdge[0], hoveredEdge[1], EDGE_HOVER_COLOR);
            } else if (potentialEdge != null) {
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawPotentialEdge(potentialEdge[0], potentialEdge[1], POTENTIAL_EDGE_COLOR);
            } else if (hoveredVertexIndex != -1) {
                // vertex hovered
                setCursor(javafx.scene.Cursor.HAND);
                draw(currentGraph);
                drawVertexHover(hoveredVertexIndex, VERTEX_HOVER_COLOR);
            } else {
                // none elements hovered
                setCursor(javafx.scene.Cursor.DEFAULT);
                if (wasHoveringVertex || wasHoveringEdge || hadPotentialEdge) {
                    draw(currentGraph);
                }
            }
        });
        
        // Clics gestion
        setOnMouseClicked(event -> {
            if (currentGraph == null) return;
            
            double mouseX = event.getX();
            double mouseY = event.getY();
            if (edgeCreationMode) {
                return;
            }
            
            // delete the edges if clicked
            if (hoveredEdge != null) {
                removeEdge(hoveredEdge[0], hoveredEdge[1]);
                hoveredEdge = null;
                draw(currentGraph);
                return;
            }
            
            // add a edges if clicked
            if (potentialEdge != null) {
                addEdge(potentialEdge[0], potentialEdge[1]);
                potentialEdge = null;
                draw(currentGraph);
                return;
            }
            
            if (hoveredVertexIndex != -1) {
                if (event.isShiftDown()) {
                    selectedVertexIndex = hoveredVertexIndex;
                    edgeCreationMode = true;
                    int rows = currentGraph.getRows();
                    int columns = currentGraph.getColumns();
                    double cellSize = calculateCellSize(rows, columns);
                    int row = selectedVertexIndex / columns;
                    int col = selectedVertexIndex % columns;
                    edgeStartX = col * cellSize + padding;
                    edgeStartY = row * cellSize + padding;
                    
                    setCursor(javafx.scene.Cursor.CROSSHAIR);
                    System.out.println("begin edges creation" + selectedVertexIndex);
                } else {
                    System.out.println("Vertex selected : " + hoveredVertexIndex);
                }
            }
        });
    }
    
    /**
     * Find a potential edge to create if the mouse hover near a edge
     */
    private void findPotentialEdge(double mouseX, double mouseY) {
        if (currentGraph == null) return;
        
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        for (int i = 0; i < currentGraph.getVertexNb(); i++) {
            int row1 = i / columns;
            int col1 = i % columns;
            double x1 = col1 * cellSize + padding;
            double y1 = row1 * cellSize + padding;
            int[] directions = {-columns, -1, 1, columns}; 
            int[] neighborIndices = new int[4];
            
            for (int d = 0; d < 4; d++) {
                int neighborIndex = i + directions[d];
                
                // Verify if the neighbor is correct
                if (neighborIndex < 0 || neighborIndex >= currentGraph.getVertexNb()) {
                    neighborIndices[d] = -1;
                    continue;
                }
                
                // Don't touch the border of the maze
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
            
            // Verify if edge is valid
            for (int j : neighborIndices) {
                if (j == -1) continue;
                
                // Verify if an edge is already there
                boolean edgeExists = false;
                for (Edges edge : currentGraph.getGraphMaze().get(i)) {
                    if (edge.getDestination() == j) {
                        edgeExists = true;
                        break;
                    }
                }
                
                if (!edgeExists) {
                    int row2 = j / columns;
                    int col2 = j % columns;
                    double x2 = col2 * cellSize + padding;
                    double y2 = row2 * cellSize + padding;
                    
                    double distance = distancePointToLine(mouseX, mouseY, x1, y1, x2, y2);
                    
                    if (distance <= HOVER_THRESHOLD) {
                        potentialEdge = new int[] {i, j};
                        return; 
                    }
                }
            }
        }
    }
    
    /**
     * Check if the mouse is near a vertex
     */
    private boolean checkVertexHover(double mouseX, double mouseY) {
        int rows = currentGraph.getRows();
        int columns = currentGraph.getColumns();
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
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
     * Check if the mouse is near an edge
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
                // Dodge duplicate terms
                if (i >= j) continue;
                
                int row2 = j / columns;
                int col2 = j % columns;
                double x2 = col2 * cellSize + padding;
                double y2 = row2 * cellSize + padding;
                
                // Distance point-line
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
     * Calcule the distance between a point and a line
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
     * Draw an effect hover near a vertex
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
     * Draw an hovered effect near an edge
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
    
    private void addEdge(int source, int destination) {
        if (graphEditor.addEdge(currentGraph, source, destination)) {
            draw(currentGraph);
            if (associatedMazeView != null) {
                associatedMazeView.draw();
            }
        }
    }

    
    /**
     * Delete and edges between two vertices
     * @param source the source vertex
     * @param destination the destination vertex
     */
    private void removeEdge(int source, int destination) {
        if (graphEditor.removeEdge(currentGraph, source, destination)) {
            draw(currentGraph);
            if (associatedMazeView != null) {
                associatedMazeView.draw();
            }
        }
    }
    /**
     * Draws the entire graph including vertices and edges.
     *
     * @param graph the graph to render
     */
    public void draw(Graph graph) {
        getChildren().clear();
        if (graph == null || graph.getGraphMaze() == null) {
            System.out.println("Error : the graph is null or invalid");
            return;
        }
        
        // Stock the current graph
        this.currentGraph = graph;

        // Recup the graph's size
        int rows = graph.getRows();
        int columns = graph.getColumns();
        int totalVertices = rows * columns;

        // Calcul to adapt the dimensions
        double cellSize = calculateCellSize(rows, columns);
        double vertexRadius = calculateVertexRadius(cellSize);
        double lineWidth = calculateLineWidth(cellSize);

        drawEdges(graph, columns, cellSize, lineWidth); 
        drawVertices(totalVertices, columns, cellSize, vertexRadius); 
    }

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
                if (destIndex < i) continue; 

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
     * Redraw the vextices of the graph
     * 
     * @param totalVertices Number of vertices
     * @param columns columns numbers
     * @param cellSize size of a cell
     * @param vertexRadius Vertex radius with a circle
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
     * Calcul the perfect size for a cell
     * 
     * @param rows rows number
     * @param columns columns numbers
     * @return size cell
     */
    private double calculateCellSize(int rows, int columns) {
        double availableWidth = FIXED_WIDTH - (2 * padding);
        double availableHeight = FIXED_HEIGHT - (2 * padding);

        double widthBasedSize = availableWidth / columns;
        double heightBasedSize = availableHeight / rows;

        return Math.min(widthBasedSize, heightBasedSize);
    }

    /**
     * calcul the best and adapted size vertex for a cell
     * 
     * @param cellSize size of a cell
     * @return Vertex radius
     */
    private double calculateVertexRadius(double cellSize) {
        double radius = cellSize * 0.25;
        return Math.max(minVertexRadius, Math.min(radius, maxVertexRadius));
    }

    /**
     * Calcul the thickness of a line
     * 
     * @param cellSize cell size
     * @return line thickness
     */
    private double calculateLineWidth(double cellSize) {
        double width = cellSize * 0.05;
        return Math.max(0.5, Math.min(width, 2.0));
    }

    /**
     * Highlights a single vertex with a default light green color.
     *
     * @param index the index of the vertex to highlight
     * @param model the graph model
     */
    public void highlightVertex(int index, Graph model) {
        draw(model); 
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


    /**
     * Highlights multiple vertices on the graph view.
     *
     * @param indices the list of vertex indices to highlight
     * @param model the graph model
     * @param color the highlight color
     */
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

    /**
     * Highlights a single vertex on the graph view.
     *
     * @param index the index of the vertex to highlight
     * @param model the graph model
     * @param color the highlight color
     */
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
     * Draw a potential edge.
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