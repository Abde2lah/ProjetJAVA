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


/**
 * Breadth-First Search (BFS) implementation for solving mazes.
 * <p>
 * This class extends {@link AbstractMazeSolver} and provides both animated
 * and non-animated BFS traversal logic. It supports integration with JavaFX views.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 */
public class BFSsolver extends AbstractMazeSolver {

    private int start = -1;
    private int end = -1;


    private boolean[] visitedVerticesArray;
    private ArrayList<Integer> vertexVisitOrder;
    Graph graph;
    private int verticesNb;

    /**
     * Default constructor.
     */
    public BFSsolver() {
        super();
        this.vertexVisitOrder = new ArrayList<>();
    }
    
    /**
     * Constructor specifying the number of vertices.
     *
     * @param verticesNb the total number of vertices in the graph
     */
    public BFSsolver(int verticesNb) {
        super();
        this.verticesNb = verticesNb;
        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<>();
    }
    
    /**
     * Sets up the BFS solver with the graph, graph view, and maze view.
     *
     * @param graph the maze graph
     * @param graphView the associated graph view (may be null)
     * @param mazeView the associated maze view (may be null)
     * @return the current solver instance
     */
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        super.setup(graph, graphView, mazeView);
        this.verticesNb = graph.getVertexNb();
        this.visitedVerticesArray = new boolean[verticesNb];
        this.graph = graph;
        this.graph = graph;
        return this;
    }

    /**
     * Core BFS algorithm implementation that returns animation steps.
     * Each step is a list of visited vertices.
     *
     * @return a list of steps representing the BFS traversal
     */
    public ArrayList<ArrayList<Integer>> solveBFS() {
        int startIdx = (mazeView != null) ? mazeView.getStartIndex() : this.start;
        int goalIdx = (mazeView != null) ? mazeView.getEndIndex() : this.end;

        this.visitedVerticesArray = new boolean[verticesNb];
        this.vertexVisitOrder = new ArrayList<>();
        ArrayList<ArrayList<Integer>> animationPath = new ArrayList<>();

        Queue<Integer> queue = new LinkedList<>();
        int[] parent = new int[verticesNb]; // To build the path at the end

        Arrays.fill(parent, -1); // parents initialization

        visitedVerticesArray[startIdx] = true;
       this.visitedVerticesNb++; 
        queue.add(startIdx);

        // Animation of the first step
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
                    this.visitedVerticesNb++;
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

        // Build of the final path in red to indicate the solution
        if (goalFound) {
            ArrayList<Integer> path = new ArrayList<>();
            int node = goalIdx;
            while (node != -1) {
                path.add(0, node);
                node = parent[node];
            }

            animationPath.add(path); 
        }else{System.out.println("No path found");}

        return animationPath;
    }
    
    /**
     * Internal BFS execution that tracks visited order and parents.
     *
     * @param startingPoint the source node
     * @param endingPoint the target node
     * @param graph the adjacency list graph structure
     * @return the path reconstructed from BFS traversal
     */
    private ArrayList<Integer> bfsWithSteps(int startingPoint, int endingPoint, ArrayList<ArrayList<Edges>> graph) {
        // Reset the structures
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
        
    /**
     * Executes the BFS algorithm with visual step-by-step animation.
     * Draws each traversal step in the MazeView.
     */
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualization inavailable in terminal mode.");
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


    /**
     * Executes the BFS algorithm without animation.
     * Still produces the steps and shows the final path in MazeView.
     */
    @Override
    public void nonAnimationVisualize() {
        if (mazeView == null) {
            System.out.println("Visualisation  inavailable in terminal mode");
            return;
        }
        
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();
        
        if (start < 0 || end < 0) {
            System.out.println("Please define a Start and end point");
            return;
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveBFS();
            
            if (steps.isEmpty()) {
                System.out.println("No path found");
                this.finalPath = new ArrayList<>();
            } else {
                this.finalPath = steps.get(steps.size() - 1);
                mazeView.nonAnimationVisualizeStep(steps);
            }
        });
        
        System.out.println("algorithm duration: " + getExecutionTime() + " ms");
    }
    
    /**
     * Executes the BFS algorithm and returns the final path between two vertices.
     *
     * @param start the starting node index
     * @param end the ending node index
     * @return a list of node indices forming the path from start to end
     */
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
        
    /**
     * Reconstructs a path from goal back to start using parent mapping.
     *
     * @param parent a map of each node to its predecessor in the path
     * @param goal the final node to trace back from
     * @return a list representing the shortest path from start to goal
 */
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
