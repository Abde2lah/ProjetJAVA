package org.mazeApp.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * The {@code Maze} class represents a maze modeled as a graph using an adjacency list.
* Each cell of the maze is a vertex, and passages between cellls are represented by edges.
* <p>
*This class is part of the model layer of the application.
* </p>
* @author Felipe Zani
* @since 1.0
* 
*/

public class Maze {

  private int VertexNb;
  
  private int EdgesNb;
    /**
     * The adjacency list representing the maze graph.
     * Each index contains a list of {@link Edges} representing connections from that vertex.
     */
  private ArrayList<ArrayList<Edges>> graphMaze;
  
    /**
     * Constructs a {@code Maze} with a randomly generated adjacency list based on a seed and size.
     * <p>
     * Each iteration randomly selects two vertices (row and column) and connects them.
     * </p>
     * 
     * @param seed the seed used for random generation
     * @param size the number of vertices to initialize the adjacency list with
     */

    public Maze(int seed, int size){
    
    this.VertexNb = 0;
    this.EdgesNb = 0;
    this.graphMaze = new ArrayList<ArrayList<Edges>>();
    
    Random rnd = new Random(seed);
    
    for(int i = 0; i<size; i++){
      this.graphMaze.add(new ArrayList<Edges>());
    } 
     
    int row = rnd.nextInt(size);
    int column = rnd.nextInt(size);

    for(int index = 0; index<size; index++){

      this.graphMaze.get(row).add(new Edges(row,column)); 
      this.graphMaze.get(column).add(new Edges(row,column));
      this.VertexNb += 2;
      this.EdgesNb++;

      row = rnd.nextInt(size);
      column = rnd.nextInt(size);  
    
    }

  }
  /**
   * Returns the number of edges in the maze.
   * 
   * @return the number of edges
   */
  public int getEdgesNb() {
      return this.EdgesNb;
  }

 /**
   * Returns the number of vertices in the maze.
   * 
   * @return the number of vertices
 */
  public int getVertexNb() {
      return this.VertexNb;
  }

    /**
     * Returns the graph representation of the maze as an adjacency list.
     * 
     * @return the adjacency list representing the maze
     */
    public ArrayList<ArrayList<Edges>> getGraphMaze() {
        return this.graphMaze;
    }
     

}
