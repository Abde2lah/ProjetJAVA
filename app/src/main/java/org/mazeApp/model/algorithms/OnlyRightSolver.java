package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;


/**
 * Maze solver using the "Always Turn Right" strategy.
 * <p>
 * This strategy attempts to solve the maze by following the right-hand rule.
 * The solver keeps a direction state and moves according to a fixed priority:
 * turn right, go forward, turn left, or turn around if stuck.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */

public class OnlyRightSolver extends AbstractMazeSolver {

    private int columns;
    private int rows;


    /**
     * Default constructor for factory use.
     */
    public OnlyRightSolver() {
        super();
    }


    /**
     * Constructor for GUI mode with MazeView.
     *
     * @param graph the maze graph
     * @param mazeView the maze visualization component
     */
    public OnlyRightSolver(Graph graph, MazeView mazeView) {
        super();
        setup(graph, null, mazeView);
    }


    /**
     * Constructor for terminal mode (headless).
     *
     * @param graph the maze graph
     * @param start index of the start node
     * @param end index of the end node
     */
    public OnlyRightSolver(Graph graph, int start, int end) {
        super();
        setup(graph, null, null);
        // Stock departure and arrival indices
        this.start = start;
        this.end = end;
    }


    /**
     * Initializes the solver with the graph and optional views.
     *
     * @param graph the maze graph
     * @param graphView (unused in this implementation)
     * @param mazeView the maze view component
     * @return the solver itself (for chaining)
     */
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        super.setup(graph, graphView, mazeView);
        if (graph != null) {
            this.columns = graph.getColumns();
            this.rows = graph.getRows();
        }
        return this;
    }

    private int start = -1;
    private int end = -1;

    /**
     * Enum representing movement directions.
     * Includes logic for turning relative to the current direction.
     */
    private enum Direction {
        RIGHT(0), DOWN(1), LEFT(2), UP(3);
        private final int value;
        Direction(int value) {
            this.value = value;
        }
        public Direction turnRight() { 
            return values()[(value + 1) % 4]; 
        }
        public Direction turnLeft() { 
            return values()[(value + 3) % 4];
        }
        public Direction turnAround() { 
            return values()[(value + 2) % 4]; 
        }
    }

    /**
     * Checks whether the given neighbor is in the specified direction from the current cell.
     *
     * @param current current cell index
     * @param neighbor neighboring cell index
     * @param direction direction to check
     * @return true if the neighbor lies in the given direction
     */
    private boolean isDirection(int current, int neighbor, Direction direction) {
        int currentRow = current / columns;
        int currentCol = current % columns;
        int neighborRow = neighbor / columns;
        int neighborCol = neighbor % columns;
        switch (direction) {
            case RIGHT: return (neighborCol == currentCol + 1) && (neighborRow == currentRow);
            case DOWN: return (neighborRow == currentRow + 1) && (neighborCol == currentCol);
            case LEFT: return (neighborCol == currentCol - 1) && (neighborRow == currentRow);
            case UP: return (neighborRow == currentRow - 1) && (neighborCol == currentCol);
            default: return false;
        }
    }

    
    /**
     * Determines the direction from one cell to another.
     *
     * @param from origin cell
     * @param to destination cell
     * @return direction of movement
     */
    private Direction getMovementDirection(int from, int to) {
        int fromRow = from / columns;
        int fromCol = from % columns;
        int toRow = to / columns;
        int toCol = to % columns;
        if (fromRow == toRow) {
            if (toCol == fromCol + 1) return Direction.RIGHT;
            if (toCol == fromCol - 1) return Direction.LEFT;
        } 
        else if (fromCol == toCol) {
            if (toRow == fromRow + 1) return Direction.DOWN;
            if (toRow == fromRow - 1) return Direction.UP;
        }
        return null;
    }

    /**
     * Attempts to move from the current cell in the given direction if unvisited.
     *
     * @param current current cell index
     * @param visited visited node tracker
     * @param direction direction to attempt
     * @return index of the neighbor if valid, otherwise -1
     */
    private int tryMove(int current, boolean[] visited, Direction direction) {
        ArrayList<ArrayList<Edges>> graphMaze = model.getGraphMaze();
        if (current < 0 || current >= graphMaze.size()) return -1;
        for (Edges edge : graphMaze.get(current)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor] && isDirection(current, neighbor, direction)) return neighbor;
        }
        return -1;
    }

    /**
     * Executes the "Always Turn Right" maze solving strategy.
     * This method returns all steps taken during the traversal for visualization.
     *
     * @return list of steps representing the path taken during resolution
     */
    public ArrayList<ArrayList<Integer>> solveRightSteps() {
        // Utiliser les valeurs de MazeView si disponibles
        int startIdx = (mazeView != null) ? mazeView.getStartIndex() : this.start;
        int goalIdx = (mazeView != null) ? mazeView.getEndIndex() : this.end;
        
        int vertexCount = model.getVertexNb();

        if (startIdx < 0 || goalIdx < 0 || startIdx >= vertexCount || goalIdx >= vertexCount) {
            System.out.println("Start and end point not defined");
            return new ArrayList<>();
        }

        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(startIdx);
        visited[startIdx] = true;

        ArrayList<Integer> path = new ArrayList<>();
        path.add(startIdx);
        allSteps.add(new ArrayList<>(path));

        Direction facing = Direction.RIGHT;

        while (!stack.isEmpty()) {
            int current = stack.peek();
            if (current == goalIdx) break;

            Direction rightDirection = facing.turnRight();
            Direction forwardDirection = facing;
            Direction leftDirection = facing.turnLeft();
            Direction backDirection = facing.turnAround();

            int next = tryMove(current, visited, rightDirection);
            if (next != -1) {
                facing = rightDirection;
            } else {
                next = tryMove(current, visited, forwardDirection);
                if (next != -1) {
                    facing = forwardDirection;
                } 
                else {
                    next = tryMove(current, visited, leftDirection);
                    if (next != -1) {
                        facing = leftDirection;
                    } 
                    else {
                        next = tryMove(current, visited, backDirection);
                        if (next != -1) facing = backDirection;
                    }
                }
            }

            if (next != -1) {
                stack.push(next);
                visited[next] = true;
                path.add(next);
                allSteps.add(new ArrayList<>(path));
            } 
            else {
                int removedVertex = stack.pop();
                if (!stack.isEmpty() && !path.isEmpty()) {
                    int previous = stack.peek();
                    Direction backtrackDir = getMovementDirection(previous, removedVertex);
                    if (backtrackDir != null) facing = backtrackDir.turnAround();
                }
                if (!path.isEmpty()) {
                    path.remove(path.size() - 1);
                    if (!allSteps.isEmpty() && !path.equals(allSteps.get(allSteps.size() - 1))) {
                        allSteps.add(new ArrayList<>(path));
                    }
                }
            }
        }

        if (!stack.isEmpty() && stack.peek() == goalIdx) {
            // Save the final path for future uses
            this.finalPath = new ArrayList<>(path);
        } else {
            this.finalPath = new ArrayList<>();
        }

        System.out.println("Path found: " + this.finalPath);
        return allSteps;
    }

    /**
     * Visualizes the path-finding process step-by-step using MazeView.
     * Useful for animated solving in GUI mode.
     */
    @Override
    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualization inavailable in terminal mode.");
            return;
        }

        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please set a start and end point.");
            return;
        }

        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRightSteps();
            if (steps.isEmpty()) {
                System.out.println("Impossible to solve the maze.");
                return;
            }
            mazeView.visualiseStep(steps);
        });

        System.out.println("Duration of Right walk solver : " + getExecutionTime() + " ms");
    }

    /**
     * Displays only the final path found without animation.
     * Used when a non-animated resolution is requested.
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
            ArrayList<ArrayList<Integer>> steps = solveRightSteps();
            
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
     * Finds the solution path between two nodes in terminal or logic-only mode.
     *
     * @param start the start vertex
     * @param end the end vertex
     * @return the final path as a list of vertex indices
     */
    @Override
    public List<Integer> findPath(int start, int end) {
        this.start = start;
        this.end = end;
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveRightSteps();
            if (!steps.isEmpty() && !steps.get(steps.size() - 1).isEmpty()) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(this.finalPath);
    }
}