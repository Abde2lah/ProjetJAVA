package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.mazeApp.model.Edges;


public class BFSsolver {

    private boolean[] visitedVerticesArray;
    private ArrayList<Integer> vertexVisitOrder;
    

    public BFSsolver(int verticesNb){
        
        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<Integer>();
        
    }
    //BFS with saving steps

    public ArrayList<Integer> bfsWithSteps(int startingPoint, int endingPoint, ArrayList<ArrayList<Edges>> graph){
        
        
        Queue <Integer> adjQueue = new LinkedList<Integer>();
        
        HashMap<Integer,Integer> parent = new HashMap<Integer,Integer>();

        this.visitedVerticesArray[startingPoint] = true;
        
        adjQueue.add(startingPoint);
    
        parent.put(0,-1);

        vertexVisitOrder.add(startingPoint);

        while (!adjQueue.isEmpty()) {
            int currentVertex = adjQueue.poll();
            
            for(Edges edg : graph.get(currentVertex)){
                
                int ajdVertex = edg.getDestination();

                if(!this.visitedVerticesArray[ajdVertex]){ 
            
                    this.visitedVerticesArray[ajdVertex] = true;
            
                    adjQueue.add(ajdVertex);

                    this.vertexVisitOrder.add(ajdVertex);
                    
                    parent.put(ajdVertex,currentVertex);
                }
            }
        }

        ArrayList<Integer> steps = reconstructPath(parent,endingPoint);
        
        return steps;
        
        
    }
    
    public void visualize(int startingPoint, int endingPoint,ArrayList<ArrayList<Edges>> graph) { 

        ArrayList<Integer> steps = bfsWithSteps(startingPoint, endingPoint, graph);

        //animateSteps(steps);


    }

    private ArrayList<Integer> reconstructPath(HashMap<Integer,Integer> parent, int goal){
        
        ArrayList<Integer> pth = new ArrayList<Integer>();
        int node = goal;
        int undefinedValue = -1;

        while(node != undefinedValue ){
            pth.add(node);
            
            try{node = parent.get(node);
            }catch(Exception  e ){
                e.printStackTrace();
                break;
            }
        }

        Collections.reverse(pth);

        return pth;
    }
}
