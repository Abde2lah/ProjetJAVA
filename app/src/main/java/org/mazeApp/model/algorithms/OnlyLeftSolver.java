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
 * Maze solver using the "Always Turn Left" strategy.
 * <p>
 * This algorithm tries to follow the left-hand rule to navigate through the maze
 * from a defined start point to an end point. It uses directional state and backtracking
 * when needed, storing each movement step-by-step.
 * </p>
 * @author Abdellah, Felipe, Jerely, Shawrov, Melina
 * @version 1.0
 */
public class OnlyLeftSolver extends AbstractMazeSolver {

    private int columns;
    private int rows;
    private int start = -1;
    private int end = -1;


    /**
     * Default constructor for factory usage.
     */
    public OnlyLeftSolver() {
        super();
    }

    /**
     * Constructor used for GUI mode with MazeView.
     *
     * @param graph the maze graph
     * @param mazeView the associated maze view
     */
    public OnlyLeftSolver(Graph graph, MazeView mazeView) {
        super();
        setup(graph, null, mazeView);
    }


    /**
     * Constructor used in terminal mode (no UI).
     *
     * @param graph the maze graph
     * @param start start index
     * @param end end index
     */
    public OnlyLeftSolver(Graph graph, int start, int end) {
        super();
        setup(graph, null, null);
        this.start = start;
        this.end = end;
    }

    /**
     * Sets up the solver with the graph and views.
     *
     * @param graph the maze graph
     * @param graphView optional graph view (can be null)
     * @param mazeView optional maze view (can be null)
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

    /**
     * Enum representing the four cardinal directions.
     * Includes methods to turn left, right, or around.
     */
    private enum Direction {
        RIGHT(0), DOWN(1), LEFT(2), UP(3);
        private final int value;
        Direction(int value) {
            this.value = value;
        }
        public Direction turnRight() { return values()[(value + 1) % 4]; }
        public Direction turnLeft() { return values()[(value + 3) % 4]; }
        public Direction turnAround() { return values()[(value + 2) % 4]; }
    }

    /**
     * Checks if a neighbor cell lies in the given direction relative to the current cell.
     *
     * @param current the current cell index
     * @param neighbor the neighboring cell index
     * @param direction the direction to check
     * @return true if neighbor is in the specified direction
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
     * Returns the direction of movement from one cell to another.
     *
     * @param from the origin cell
     * @param to the destination cell
     * @return the direction from 'from' to 'to'
     */
    private Direction getMovementDirection(int from, int to) {
        int fromRow = from / columns;
        int fromCol = from % columns;
        int toRow = to / columns;
        int toCol = to % columns;
        if (fromRow == toRow) {
            if (toCol == fromCol + 1) return Direction.RIGHT;
            if (toCol == fromCol - 1) return Direction.LEFT;
        } else if (fromCol == toCol) {
            if (toRow == fromRow + 1) return Direction.DOWN;
            if (toRow == fromRow - 1) return Direction.UP;
        }
        return null;
    }


    /**
     * Attempts to move in the given direction if the neighbor exists and is not visited.
     *
     * @param current the current cell index
     * @param visited array of visited flags
     * @param direction the direction to attempt
     * @return the neighbor index or -1 if move is invalid
     */
    private int tryMove(int current, boolean[] visited, Direction direction) {
        ArrayList<ArrayList<Edges>> graphMaze = model.getGraphMaze();
        if (current < 0 || current >= graphMaze.size()) return -1;
        for (Edges edge : graphMaze.get(current)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor] && isDirection(current, neighbor, direction)) {
              this.visitedVerticesNb++;
              return neighbor;}
        }
        return -1;
    }

    /**
     * Attempts to move in the given direction if the neighbor exists and is not visited.
     *
     * @param current the current cell index
     * @param visited array of visited flags
     * @param direction the direction to attempt
     * @return the neighbor index or -1 if move is invalid
     */
    public ArrayList<ArrayList<Integer>> solveLeftSteps() {
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

            Direction leftDirection = facing.turnLeft();
            Direction forwardDirection = facing;
            Direction rightDirection = facing.turnRight();
            Direction backDirection = facing.turnAround();

            int next = tryMove(current, visited, leftDirection);
            if (next != -1) {
                facing = leftDirection;
            } else {
                next = tryMove(current, visited, forwardDirection);
                if (next != -1) {
                    facing = forwardDirection;
                } else {
                    next = tryMove(current, visited, rightDirection);
                    if (next != -1) {
                        facing = rightDirection;
                    } else {
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
            } else {
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
            // Sauvegarde du chemin final pour référence future
            this.finalPath = new ArrayList<>(path);
        } else {
            this.finalPath = new ArrayList<>();
        }

        System.out.println("Path found: " + this.finalPath);
        return allSteps;
    }

    /**
     * Executes the algorithm and animates each step on the maze view.
     * The path is shown incrementally with delays between each step.
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
            ArrayList<ArrayList<Integer>> steps = solveLeftSteps();
            if (steps.isEmpty()) {
                System.out.println("Impossible to solve the maze.");
                return;
            }
            mazeView.visualiseStep(steps);
        });

        System.out.println("Duration of Left walk solver : " + getExecutionTime() + " ms");
    }


    /**
     * Executes the algorithm and displays the final path without step-by-step animation.
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
            ArrayList<ArrayList<Integer>> steps = solveLeftSteps();
            
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
     * Finds the final path between two nodes using the left-hand rule.
     * This version is used in headless or terminal mode.
     *
     * @param start the start vertex
     * @param end the end vertex
     * @return the final path found as a list of vertex indices
     */
    @Override
    public List<Integer> findPath(int start, int end) {
        this.start = start;
        this.end = end;
        
        measureExecutionTime(() -> {
            ArrayList<ArrayList<Integer>> steps = solveLeftSteps();
            if (!steps.isEmpty() && !steps.get(steps.size() - 1).isEmpty()) {
                this.finalPath = steps.get(steps.size() - 1);
            } else {
                this.finalPath = new ArrayList<>();
            }
        });
        
        return new ArrayList<>(this.finalPath);
    }
}
