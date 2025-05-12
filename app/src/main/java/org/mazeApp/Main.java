package org.mazeApp;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Maze;


/**
* 
* @author Abdellah,Felipe, Jeremy, Shawrov
* 
*/


public class Main{
  public static void main(String[] args) {
     Maze test = new Maze(2055,4);
    ArrayList<ArrayList<Edges>> arr = test.getGraphMaze();

    System.out.println(test.getGraphMaze().size());
    int count = 0;
    for(ArrayList<Edges> arrRow : arr){
      System.out.println("size Row "+count+" : "+arrRow.size());
      for(Edges eg : arrRow){
        System.out.println(eg.getSource()+"-> "+eg.getDestination());
      }
      count++;
    }

  } 


}
