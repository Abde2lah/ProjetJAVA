package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

public class BFSsolver extends AbstractMazeSolver {

    private boolean[] visitedVerticesArray;
    private ArrayList<Integer> vertexVisitOrder;
    private int verticesNb;

    public BFSsolver() {
        super();
        this.vertexVisitOrder = new ArrayList<>();
    }
    
    public BFSsolver(int verticesNb) {
        super();
        this.verticesNb = verticesNb;
        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<>();
    }
    
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        super.setup(graph, graphView, mazeView);
        this.verticesNb = graph.getVertexNb();
        this.visitedVerticesArray = new boolean[verticesNb];
        return this;
    }
    
    //BFS with saving steps
    private ArrayList<Integer> bfsWithSteps(int startingPoint, int endingPoint, ArrayList<ArrayList<Edges>> graph) {
        // Réinitialiser les structures
        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<>();
        
        Queue<Integer> adjQueue = new LinkedList<>();
        HashMap<Integer, Integer> parent = new HashMap<>();
        
        this.visitedVerticesArray[startingPoint] = true;
        adjQueue.add(startingPoint);
        parent.put(startingPoint, -1);
        
        vertexVisitOrder.add(startingPoint);
        
        while (!adjQueue.isEmpty()) {
            int currentVertex = adjQueue.poll();
            
            for (Edges edg : graph.get(currentVertex)) {
                int ajdVertex = edg.getDestination();
                
                if (!this.visitedVerticesArray[ajdVertex]) {
                    this.visitedVerticesArray[ajdVertex] = true;
                    adjQueue.add(ajdVertex);
                    this.vertexVisitOrder.add(ajdVertex);
                    parent.put(ajdVertex, currentVertex);
                }
            }
        }
        
        return reconstructPath(parent, endingPoint);
    }
    
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("MazeView is null. Cannot visualize.");
            return;
        }
        
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();
        
        measureExecutionTime(() -> {
            this.finalPath = bfsWithSteps(start, end, model.getGraphMaze());
            
            // Pour BFS, nous n'avons pas d'étapes intermédiaires, 
            // alors nous devons les simuler pour la visualisation
            ArrayList<ArrayList<Integer>> steps = new ArrayList<>();
            for (int i = 0; i < finalPath.size(); i++) {
                ArrayList<Integer> step = new ArrayList<>(finalPath.subList(0, i + 1));
                steps.add(step);
            }
            
            if (!steps.isEmpty()) {
                mazeView.visualiseStep(steps);
            }
        });
        
        System.out.println("BFS algorithm duration : " + getExecutionTime() + " ms");
    }
    
    @Override
    public List<Integer> findPath(int start, int end) {
        if (model == null) {
            System.out.println("Graph model is null. Cannot find path.");
            return new ArrayList<>();
        }
        
        measureExecutionTime(() -> {
            this.finalPath = bfsWithSteps(start, end, model.getGraphMaze());
        });
        
        return new ArrayList<>(finalPath);
    }
    
    private ArrayList<Integer> reconstructPath(HashMap<Integer, Integer> parent, int goal) {
        ArrayList<Integer> pth = new ArrayList<>();
        int node = goal;
        int undefinedValue = -1;
        
        while (node != undefinedValue) {
            pth.add(node);
            
            try {
                node = parent.get(node);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        
        Collections.reverse(pth);
        return pth;
    }
}
