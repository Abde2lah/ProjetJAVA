package org.mazeApp.model;

import java.util.ArrayList;

import org.mazeApp.model.generator.MazeGenerator;

import org.mazeApp.model.generator.KruskalGenerator;


/**

 * Representation of a graph using an adjacency list.
 * This class represents a graph using an adjacency list, where each vertex
 * is represented by an index and its neighbors are stored in an ArrayList.
 * @author Felipe Zani, Jeremy Perbost
 * @since 1.0
 */
public class Graph {

    private int vertexCount;
    private int edgeCount;
    private int rows;
    private int columns;
    private ArrayList<ArrayList<Edges>> graphMaze;
    private ArrayList<Edges> generationSteps;
    private static MazeGenerator currentGenerator = new KruskalGenerator();
    private int seed;

    /**
     * Return the generation steps of the maze.
     */
    public ArrayList<Edges> getGenerationSteps() {
        return generationSteps;
    }

    /**
     * Main constructor for a rectangular grid maze with kruskal generator.
     * 
     * @param seed The seed for random generation
     * @param rows Number of rows in the grid
     * @param columns Number of columns in the grid
     */
    public Graph(int seed, int rows, int columns) {
        int totalVertices = rows * columns;

        this.vertexCount = totalVertices;
        this.rows = rows;
        this.columns = columns;
        this.edgeCount = 0;
        this.graphMaze = new ArrayList<>();
        this.generationSteps = new ArrayList<>();
        this.seed = seed; // Stockage de la graine

        initializeGraph(totalVertices);
        generateGridMaze(seed, rows, columns);
    }

    public void getAllNeighbours(){
        for(ArrayList<Edges> arrayEdges : graphMaze){
            for(Edges edge : arrayEdges){
                System.out.println(edge);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Constructor for a square grid maze.
     * 
     * @param seed 
     * @param size 
     */
    public Graph(int seed, int size) {
        this(seed, size, size);
    }

    /**
     * Factory method to create an empty graph.
     */
    public static Graph emptyGraph(int rows, int columns) {
        return new Graph(true, rows, columns);
    }

    /**
     * Intern Constructor for empty graph.
     */
    private Graph(boolean empty, int rows, int columns) {
        int totalVertices = rows * columns;
        this.vertexCount = totalVertices;
        this.rows = rows;
        this.columns = columns;
        this.edgeCount = 0;
        this.graphMaze = new ArrayList<>();
        this.generationSteps = new ArrayList<>();
        this.seed = 0; // Valeur par défaut pour un graphe vide
        
        initializeGraph(totalVertices);
        
        // Don't generate the maze if empty is true
        if (!empty) {
            generateGridMaze(this.seed, rows, columns);
        }
    }

    /**
     * Initialize the graph with empty adjacency lists.
     */
    private void initializeGraph(int totalVertices) {
        for (int i = 0; i < totalVertices; i++) {
            this.graphMaze.add(new ArrayList<>());
        }
    }

    /**
     * Generate a maze using the current generator.
     */
    private void generateGridMaze(int seed, int rows, int columns) {
        generationSteps = currentGenerator.generate(rows, columns, seed);
        
        // Build the graph using the edges generated
        for (Edges edge : generationSteps) {
            addEdgeBidirectional(edge.getSource(), edge.getDestination());
        }
    }
    // Add an edge to the graph
    private void addEdgeBidirectional(int source, int destination) {
        this.graphMaze.get(source).add(new Edges(source, destination));
        this.graphMaze.get(destination).add(new Edges(destination, source));
        this.edgeCount++;
    }

    public void removeVertex(int vertex) {
        int edgesToRemove = graphMaze.get(vertex).size();
        graphMaze.remove(vertex);
        vertexCount--;
        edgeCount -= edgesToRemove;

        for (ArrayList<Edges> edges : graphMaze) {
            edges.removeIf(edge -> edge.getDestination() == vertex);

            for (Edges edge : new ArrayList<>(edges)) {
                if (edge.getDestination() > vertex) {
                    int newDest = edge.getDestination() - 1;
                    edges.remove(edge);
                    edges.add(new Edges(edge.getSource(), newDest));
                }
            }
        }
    }

    public void clearGraph() {
        for (ArrayList<Edges> edges : graphMaze) {
            edges.clear();
        }
        edgeCount = 0;
    }

    public int getEdgesNb() {
        return this.edgeCount;
    }

    public int getVertexNb() {
        return this.vertexCount;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }
    
    public void addEdge(int source, int destination) {
        this.graphMaze.get(source).add(new Edges(source, destination));
        this.graphMaze.get(destination).add(new Edges(destination, source));
        this.edgeCount++;
    }
    public ArrayList<ArrayList<Edges>> getGraphMaze() {
        return this.graphMaze;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Labyrinthe %dx%d\n", rows, columns));
        sb.append(String.format("Nombre de sommets : %d\n", vertexCount));
        sb.append(String.format("Nombre d’arêtes : %d\n\n", edgeCount));
        sb.append("Structure du graphe :\n");
        sb.append("---------------------\n");

        for (int i = 0; i < graphMaze.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            sb.append(String.format("Sommet %2d (%d,%d) : ", i, row, col));

            if (graphMaze.get(i).isEmpty()) {
                sb.append("(pas de connexion)");
            } else {
                for (Edges edge : graphMaze.get(i)) {
                    int destRow = edge.getDestination() / columns;
                    int destCol = edge.getDestination() % columns;
                    sb.append(String.format("→ %2d (%d,%d) ", edge.getDestination(), destRow, destCol));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Défine the current generator
     */
    public static void setGenerator(MazeGenerator generator) {
        currentGenerator = generator;
    }

    /**
     * Return the current generator
     */
    public static MazeGenerator getCurrentGenerator() {
        return currentGenerator;
    }

    /**
     * Get the seed value used for generating this maze.
     * 
     * @return The seed value
     */
    public int getSeed() {
        return this.seed;
    }

    /**
     * Set a new seed value for this maze.
     * Note: This does not regenerate the maze automatically.
     * 
     * @param seed The new seed value
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Retourne toutes les arêtes du graphe, sans doublons (chaque arête une seule fois).
     */
    public ArrayList<Edges> getEdges() {
        ArrayList<Edges> edgesList = new ArrayList<>();
        boolean[][] seen = new boolean[vertexCount][vertexCount];

        for (int u = 0; u < graphMaze.size(); u++) {
            for (Edges edge : graphMaze.get(u)) {
                int v = edge.getDestination();
                if (!seen[u][v] && !seen[v][u]) {
                    edgesList.add(edge);
                    seen[u][v] = true;
                    seen[v][u] = true;
                }
            }
        }

        return edgesList;
    }

    /**
     * Returns all edges (connections) adjacent to the specified vertex.
     * This method finds all edges where the specified vertex is either the source
     * or the destination.
     *
     * @param vertex The vertex for which to find neighboring edges
     * @return ArrayList of Edges that are connected to the specified vertex
     * @throws IllegalArgumentException if the vertex is outside the valid range
     */
    public ArrayList<Edges> getEdges(int vertex) {
        // Check if vertex is valid
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Invalid vertex: " + vertex + ". Must be between 0 and " + (vertexCount - 1));
        }
        
        // Create a new ArrayList to store all neighboring edges
        ArrayList<Edges> neighborEdges = new ArrayList<>();
        
        // Add all edges where the specified vertex is the source
        neighborEdges.addAll(graphMaze.get(vertex));
        
        // Return the list of neighboring edges
        return neighborEdges;
    }

}
