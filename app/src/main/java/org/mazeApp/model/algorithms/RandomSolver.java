package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;


/**
 * Maze solver using the Random Walk algorithm.
 * <p>
 * This solver explores the maze by randomly choosing an unvisited neighboring node
 * until the goal is reached or all options are exhausted.
 * It is not guaranteed to find a path if one exists.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class RandomSolver extends AbstractMazeSolver {

    private int start = -1;
    private int end = -1;

    /**
     * Default constructor for use in solver factory.
     */
    public RandomSolver() {
        super();
    }

    /**
     * Constructor for GUI mode using MazeView.
     *
     * @param graph the maze graph
     * @param mazeView the maze display view
     */
    public RandomSolver(Graph graph, MazeView mazeView) {
        super();
        setup(graph, null, mazeView);
    }

    /**
     * Constructor for terminal mode (non-GUI).
     *
     * @param graph the maze graph
     * @param start starting node index
     * @param end ending node index
     */
    public RandomSolver(Graph graph, int start, int end) {
        super();
        setup(graph, null, null);
        this.start = start;
        this.end = end;
    }


    /**
     * Solves the maze using a random walk strategy.
     * It randomly picks an unvisited neighbor until the end is reached or the path is blocked.
     *
     * @return a list of steps taken during the solving process, including backtracking
     */
    public ArrayList<ArrayList<Integer>> solveRandomWalkSteps() {
        // Use the values of the maze if available
        int startIdx = (mazeView != null) ? mazeView.getStartIndex() : this.start;
        int goalIdx = (mazeView != null) ? mazeView.getEndIndex() : this.end;
        
        int vertexCount = model.getVertexNb();
        ArrayList<ArrayList<Edges>> graphMaze = model.getGraphMaze();

        Random rand = new Random();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        ArrayList<Integer> path = new ArrayList<>();
        path.add(startIdx);
        visited[startIdx] = true;
        this.visitedVerticesNb++;
        allSteps.add(new ArrayList<>(path)); // Add the first step

        while (!path.isEmpty()) {
            int current = path.get(path.size() - 1);

            if (current == goalIdx) {
                this.finalPath = new ArrayList<>(path);
                return allSteps;
            }

            ArrayList<Integer> unvisitedNeighbours = new ArrayList<>();
            for (Edges edge : graphMaze.get(current)) {
                int neighbor = edge.getDestination();
                if (!visited[neighbor]) {
                  this.visitedVerticesNb++;
                  unvisitedNeighbours.add(neighbor);
                }
            }

            if (!unvisitedNeighbours.isEmpty()) {
                int next = unvisitedNeighbours.get(rand.nextInt(unvisitedNeighbours.size()));
                path.add(next);
                visited[next] = true;
                allSteps.add(new ArrayList<>(path));
            } else {
                path.remove(path.size() - 1);
                allSteps.add(new ArrayList<>(path)); 
            }
        }
        
        // No path found
        this.finalPath = new ArrayList<>();
        return allSteps;
    }

    /**
     * Launches the maze resolution with animation using MazeView.
     * Each step is visualized in real-time.
     */
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualization inavailable in terminal mode");
            return;
        }

        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please define a start and end point.");
            return;
        }

        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
            mazeView.visualiseStep(steps);
        });
        
        System.out.println("Algorithm duration of Random Walk : " + getExecutionTime() + " ms");
        System.out.println("Path found: " + getFinalPath());
    }

    /**
     * Executes the resolution without animation, displaying only the final result.
     */
    @Override
    public void nonAnimationVisualize() {
        if (mazeView == null) {
            System.out.println("MazeView is null. Cannot visualize.");
            return;
        }
        
        int start = mazeView.getStartIndex();
        int end = mazeView.getEndIndex();
        
        if (start < 0 || end < 0) {
            System.out.println("Please define a Start and end point");
            return;
        }
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
            
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
     * Finds a path from start to end using random walk strategy, without visualization.
     * May not find a path even if one exists.
     *
     * @param start index of the starting node
     * @param end index of the target node
     * @return the final path found, or an empty list if no path exists
     */
    @Override
    public List<Integer> findPath(int start, int end) {
        this.start = start;
        this.end = end;
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRandomWalkSteps();
            if (!steps.isEmpty() && steps.get(steps.size() - 1).contains(end)) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(this.finalPath);
    }
}
