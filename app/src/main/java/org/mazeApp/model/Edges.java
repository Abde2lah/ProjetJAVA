package org.mazeApp.model;

/**
 * The {@code Edges} class represents a connection between two vertices in the maze graph.
 * Each edge connects a source vertex to a destination vertex, enabling the graph to be
 * either undirected or directed depending on how edges are added.
 * <p>
 * This class is part of the model layer of the application.
 * </p>
 * 
 * @author Felipe Zani
 * @since 1.0
 */


public class Edges {

  //The source vertex of the edge.
  private int source;
  //The destination vertex of the edge.
  private int destination;
  

  /**
 * Constructs an {@code Edges} object with the specified source and destination vertices.
 * If either vertex is negative, it defaults to -1.
 * 
 * @param src  the source vertex
 * @param dest the destination vertex
 */

  public Edges(int src , int dest){
    this.source = src; 
    this.destination = dest;
  }
  

  /**
   * Returns the source vertex of this edge.
  * @return the source Vertex of a specific Edge
  */

  public int getSource() {
      return this.source;
  }
/**
 * Returns the destination vertex of this edge.
 *@return the destination of this vertex edge.
 * */
 public int getDestination() {
      return this.destination;
  }
  

}

