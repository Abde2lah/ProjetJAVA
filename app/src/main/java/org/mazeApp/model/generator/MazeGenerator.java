package org.mazeApp.model.generator;

import java.util.ArrayList;

import org.mazeApp.model.Edges;


/**
 *This interface is intended to be implemented in order to specify the maze generator methods.
 * @since 1.0
 */
public abstract class MazeGenerator {
    /**
     *Enumeration containing booleans where each field represents the type of maze generated 
     * 
     * */
    protected enum mazeType {
      PERFECT(false),
      IMPERFECT(true);

      private final boolean code;
      
      mazeType (boolean code){
        this.code = code ;  
      }

      public boolean getCode() {
        return code;
      } 
    }  
  /**
     * @param rows Number of rows in the Maze
     * @param columns Number of columns in the Maze
     * @param seed Seed Number of the Maze
     * @return Returns an ArrayList of {@link org.mazeApp.model.Edges} representing the maze
     */
    abstract public ArrayList<Edges> generate(int rows, int columns, int seed);
    
    /**
     * @return Returns the algorithm's name used for the maze genaration proccess. 
     * */
    abstract public String getName();
}
