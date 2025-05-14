package org.mazeApp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;

/**
 * Representation of a maze as a graph.
 * Uses an adjacency list to model the connections between cells.
 * The graph is acyclic and planar (no crossing edges).
 * 
 * @author Felipe Zani, Jeremy Perbost
 */

public class Graph {

    private int vertexCount;
    private int edgeCount;
    private int rows;
    private int columns;
    private ArrayList<ArrayList<Edges>> graphMaze;
   
    /**
     * Constructs a random maze using a modified Kruskal's algorithm.
     * The maze is represented by a planar graph (no crossing edges).
     * 
     * @param seed The seed for random generation
     * @param rows Number of rows in the grid.
     * @param columns Number of columns in the grid
     */

    public Graph(int seed, int rows, int columns) {
      
      int totalVertices = rows * columns; 
      
      this.vertexCount = totalVertices;
      this.rows = rows;
      this.columns = columns;
      this.edgeCount = 0;
      this.graphMaze = new ArrayList<>();

      initializeGraph(totalVertices);
      
      // Generate the maze using a modified Kruskal's algorithm for grids
      generateGridMaze(seed, rows, columns);
    }
    
    /**
    * Simplified constructor to create a square maze.
    * 
    * @param seed The seed for random generation
    * @param size Number of cells per side of the grid (total size = size*size)
    */
    public Graph(int seed, int size) {
        this(seed, size, size);
    }
    
    /**
     * Initializes an empty graph.
     * @param totalVertices Number of total Vertices in the Graph. 
     */
    private void initializeGraph(int totalVertices) {
        for (int i = 0; i < totalVertices; i++) {
            this.graphMaze.add(new ArrayList<>());
        }
    }
    /**
    * Generates a maze on a rectangular grid.
    * This method ensures the graph is planar (no crossing edges).
    *@param seed The seed for random generation.
    *@param rows Number of rows in the Graph.
    *@param columns Number of columns in the Graph. 
    */ 
    
    private void generateGridMaze(int seed, int rows, int columns) {
     
      // Create all possible edges in a grid
      ArrayList<Edges> allEdges = createGridEdges(rows , columns );
      // Shuffle edges  pseudorandomly in order to assure that one seed will result one maze. 
      Collections.shuffle(allEdges, new Random(seed));
      // Apply Kruskal's algorithm to create a spanning tree 
      applyKruskalAlgorithm(allEdges, rows * columns);
    }

    /**
      * Creates the possible edges in a grid of rows x columns.
      * Each vertex is connected only to its orthogonal neighbors (top, bottom, left, right).
      * @param rows 
      * @param columns
      *
      * @return Returns a list of edges containing of the Graph.
    */
    private ArrayList<Edges> createGridEdges(int rows, int columns) {
        ArrayList<Edges> edges = new ArrayList<>();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int current = row * columns + col;
                
                // Connect to the cell on the right (if it exists)
                if (col < columns - 1) {
                    edges.add(new Edges(current, current + 1));
                }
                
                // Connect to the cell below (if it exists)
                if (row < rows - 1) {
                    edges.add(new Edges(current, current + columns));
                }
            }
        }
        return edges;
    }
    /**
    * Applies Kruskal's algorithm to create a minimum spanning tree.
    * @param edges An ArrayList containing all edges of the Graph.
    * @param totalVertices Number of the Vertices in the Graph.
    */
    
    private void applyKruskalAlgorithm(ArrayList<Edges> edges, int totalVertices) {
    
    int[] parent = new int[totalVertices];

    // Initialize each vertex as its own parent
    for (int i = 0; i < totalVertices; i++) {
        parent[i] = i;
    }

    // Iterate through all sorted edges (randomly shuffled)
    for (Edges edge : edges) {
        int source = edge.getSource();
        int destination = edge.getDestination();

        // Check if adding this edge would create a cycle
        int sourceRoot = find(parent, source);
        int destRoot = find(parent, destination);
        if (sourceRoot != destRoot) {
            // Add the edge to the graph
            addEdgeBidirectional(source, destination);
            // Merge the two components
            union(parent, sourceRoot, destRoot);

            // Stop when we have a spanning tree (n-1 edges)
            if (this.edgeCount == totalVertices - 1) {
                break;
            }
        }
    }
}

  /**
   * Finds the root of a vertex in the Union-Find data structure.
   * @param parent Array of Integers containing their respective Parent
   * @param vertex Integer which the function is searching for.
   *
   * @return The parent Node of a disjoint set of items.
   */
  private int find(int[] parent, int vertex) {
    if (parent[vertex] != vertex) {
        parent[vertex] = find(parent, parent[vertex]);
    }
    return parent[vertex];
  }

  /**
  * Merges two components in the Union-Find data structure.
  * @param Array of Integers containing their respective parent.
  * @param Integer representing Parent Vertex.
  * @param Integer representing Child Vertex.
  */
  private void union(int[] parent, int x, int y) {
    parent[x] = y;
  }

/**
 * Adds a bidirectional edge between two vertices.
 * @param source Integer value of the source node.
 * @param destination Integer value of the destination node.
 */
private void addEdgeBidirectional(int source, int destination) {
    this.graphMaze.get(source).add(new Edges(source, destination));
    this.graphMaze.get(destination).add(new Edges(destination, source));
    this.edgeCount++;
}

/**
 * Adds a bidirectional edge between two vertices.
 * 
 */
public void addEdge(int source, int destination) {
    graphMaze.get(source).add(new Edges(source, destination));
    graphMaze.get(destination).add(new Edges(destination, source));
    edgeCount++;
}

/**
 * Removes an edge between two vertices.
 * @param Integer representaiton of a source node.
 * @param Integer representation of destination node.
 */
public void deleteEdge(int source, int destination) {
    graphMaze.get(source).removeIf(edge -> edge.getDestination() == destination);
    graphMaze.get(destination).removeIf(edge -> edge.getDestination() == source);
    edgeCount--;
}

/**
 * Adds a new vertex to the graph.
 */
public void addVertex() {
    graphMaze.add(new ArrayList<Edges>());
    vertexCount++;
}

/**
 * Removes a vertex from the graph and all its connections.
 * @param vertex Number of vertex in the Graph.
 */
public void removeVertex(int vertex) {
    // Count the number of edges to remove
    int edgesToRemove = graphMaze.get(vertex).size();

    // Remove the vertex
    graphMaze.remove(vertex);
    vertexCount--;
    edgeCount -= edgesToRemove;

    // Update references in the other vertices
    for (ArrayList<Edges> edges : graphMaze) {
        // Remove connections to this vertex
        edges.removeIf(edge -> edge.getDestination() == vertex);

        // Update indices of vertices greater than the one removed
        for (Edges edge : new ArrayList<>(edges)) {
            if (edge.getDestination() > vertex) {
                // This implementation is not ideal because Edges is immutable
                // We would need to modify Edges to allow value changes
                int newDest = edge.getDestination() - 1;
                edges.remove(edge);
                edges.add(new Edges(edge.getSource(), newDest));
            }
        }
    }
}

/**
 * Clears the graph by removing all edges.
 */
public void clearGraph() {
    for (ArrayList<Edges> edges : graphMaze) {
        edges.clear();
    }
    edgeCount = 0;
}

/**
 * Returns the number of edges in the graph.
 * @return Total number of edges.
 */
public int getEdgesNb() {
    return this.edgeCount;
}

/**
 * Returns the number of vertices in the graph.
 * @return Total number of Vertices.
 */
public int getVertexNb() {
    return this.vertexCount;
}

/**
 * Returns the number of rows in the maze.
 * @return Total nylber of rows.
 */
public int getRows() {
    return this.rows;
}

/**
 * Returns the number of columns in the maze.
 */
public int getColumns() {
    return this.columns;
}

/**
 * Returns the graph structure as an adjacency list.
 */
public ArrayList<ArrayList<Edges>> getGraphMaze() {
    return this.graphMaze;
}

/**
 * Textual representation of the graph.
 */
@Override
public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(String.format("Labyrinthe %dx%d\n", rows, columns));
    sb.append(String.format("Nombre de sommets : %d\n", vertexCount));
    sb.append(String.format("Nombre de arêtes : %d\n\n", edgeCount));

    sb.append("Structure du graph:\n");
    sb.append("------------------\n");

    for (int i = 0; i < graphMaze.size(); i++) {
        int row = i / columns;
        int col = i % columns;
        sb.append(String.format("Sommets %2d (%d,%d) : ", i, row, col));

        if (graphMaze.get(i).isEmpty()) {
            sb.append("(pas de connection)");
        } else {
            for (int j = 0; j < graphMaze.get(i).size(); j++) {
                Edges edge = graphMaze.get(i).get(j);
                int destRow = edge.getDestination() / columns;
                int destCol = edge.getDestination() % columns;
                sb.append(String.format("→ %2d (%d,%d) ", edge.getDestination(), destRow, destCol));
            }
        }
        sb.append("\n");
    }

    return sb.toString();
  }

  
}
