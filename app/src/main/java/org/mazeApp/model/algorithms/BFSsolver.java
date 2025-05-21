package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
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

    private int start = -1;
    private int end = -1;

    private boolean[] visitedVerticesArray;
    private ArrayList<Integer> vertexVisitOrder;
    Graph graph;
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
        this.graph = graph;
        return this;
    }

    public ArrayList<ArrayList<Integer>> solveBFS() {
        int startIdx = (mazeView != null) ? mazeView.getStartIndex() : this.start;
        int goalIdx = (mazeView != null) ? mazeView.getEndIndex() : this.end;

        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<>();
        ArrayList<ArrayList<Integer>> animationPath = new ArrayList<>();

        Queue<Integer> queue = new LinkedList<>();
        int[] parent = new int[verticesNb]; // Pour reconstruire le chemin à la fin

        Arrays.fill(parent, -1); // Initialisation des parents

        visitedVerticesArray[startIdx] = true;
        queue.add(startIdx);

        // Animation de la première étape (départ)
        ArrayList<Integer> initialStep = new ArrayList<>();
        initialStep.add(startIdx);
        animationPath.add(new ArrayList<>(initialStep));

        boolean goalFound = false;

        while (!queue.isEmpty() && !goalFound) {
            int current = queue.poll();
            ArrayList<Integer> step = new ArrayList<>();
            step.add(current); 

            for (Edges edge : graph.getEdges(current)) {
                int neighborSource = edge.getDestination();
                int neighborFirst = edge.getSource();

                if (!visitedVerticesArray[neighborSource]) {
                    visitedVerticesArray[neighborSource] = true;
                    parent[neighborSource] = current;
                    queue.add(neighborSource);

                    if (step.isEmpty() || step.get(step.size() - 1) != neighborFirst) {
                        step.add(neighborFirst);
                    }
                    if (step.isEmpty() || step.get(step.size() - 1) != neighborSource) {
                        step.add(neighborSource);
                    }

                    if (neighborSource == goalIdx) {
                        goalFound = true;
                        break;
                    }
                }
            }

            if (!step.isEmpty()) {
                animationPath.add(new ArrayList<>(step));
            }
        }

        // Reconstruction du chemin de fin pour le montrer en rouge à la fin
        if (goalFound) {
            ArrayList<Integer> path = new ArrayList<>();
            int node = goalIdx;
            while (node != -1) {
                path.add(0, node);
                node = parent[node];
            }

            animationPath.add(path); 
        }

        return animationPath;
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
            System.out.println("Visualisation non disponible en mode terminal.");
            return;
        }

        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please define a start and end point.");
            return;
        }

        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveBFS();
            mazeView.visualiseStep(steps);
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