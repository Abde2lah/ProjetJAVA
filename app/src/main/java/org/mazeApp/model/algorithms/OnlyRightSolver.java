package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

public class OnlyRightSolver {

    private ArrayList<ArrayList<Edges>> graphMaze;
    private Graph graph;
    private MazeView mazeView;
    private int start;
    private int goal;
    private int vertexCount;
    private int columns;
    private int rows;

    /**
     * 
     * 
     * @param graph The graph representing the maze
     * @param mazeView The view for visualizing the maze
     */
    public OnlyRightSolver(Graph graph, MazeView mazeView) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.columns = graph.getColumns();
        this.rows = graph.getRows();
        this.mazeView = mazeView;
        
    }

    /**
     * Cardnial directions for the right-hand rule
     */
    private enum Direction {
        RIGHT(0),    // East
        DOWN(1),     // South
        LEFT(2),     // West
        UP(3);       // North
        
        private final int value;
        
        Direction(int value) {
            this.value = value;
        }
        
        /**
         * Calcul the direction after a right turn
         */
        public Direction turnRight() {
            return values()[(value + 1) % 4];
        }
        
        /**
         * Same as turnRight but for left
         */
        public Direction turnLeft() {
            return values()[(value + 3) % 4]; 
        }
        
        /**
         * Same as turnRight but for turnback
         */
        public Direction turnAround() {
            return values()[(value + 2) % 4];
        }
    }

    /**
     * Decide if the movement between two vertices is in the desired direction
     * 
     * @param current The current vertex
     * @param neighbor The neighbor vertex potentially in the desired direction
     * @param direction The desired direction
     * @return true if the movement corresponds to the direction
     */
    private boolean isDirection(int current, int neighbor, Direction direction) {
        int currentRow = current / columns;
        int currentCol = current % columns;
        int neighborRow = neighbor / columns;
        int neighborCol = neighbor % columns;
            
        switch (direction) {
            case RIGHT:
                return (neighborCol == currentCol + 1) && (neighborRow == currentRow);
            case DOWN:
                return (neighborRow == currentRow + 1) && (neighborCol == currentCol);
            case LEFT:
                return (neighborCol == currentCol - 1) && (neighborRow == currentRow);
            case UP:
                return (neighborRow == currentRow - 1) && (neighborCol == currentCol);
            default:
                return false;
        }
    }

    /**
     * Decide of the movement between two vertices is in the desired direction
     * 
     * @param from Start vertex
     * @param to End vertex
     * @return Direction of the movement (UP, DOWN, LEFT, RIGHT) or null if not adjacent
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
        
        return null; // The vertices are not adjacent
    }

    /**
     * Try to move in the specified direction from the current vertex
     * 
     * @param current Current vertex
     * @param visited Table of visited vertices
     * @param direction Favorite direction to move
     * @return The next vertex in the specified direction or -1 if not possible
     */
    private int tryMove(int current, boolean[] visited, Direction direction) {
        if (current < 0 || current >= graphMaze.size()) {
            return -1;
        }
        
        for (Edges edge : graphMaze.get(current)) {
            int neighbor = edge.getDestination();
            
            if (!visited[neighbor] && isDirection(current, neighbor, direction)) {
                return neighbor;
            }
        }
        
        return -1;  // No valid move in the specified direction
    }

    /**
     * Solve the maze using the right-hand rule
     * 
     * @return  Steps of the path taken
     */
    public ArrayList<ArrayList<Integer>> solveRightSteps() {
        // Initialization
        this.start = mazeView.getStartIndex();
        this.goal = mazeView.getEndIndex();
        
        // Verify if the start and goal points are valid
        if (start < 0 || goal < 0 || start >= vertexCount || goal >= vertexCount) {
            System.out.println("Start and end point not defined");
            return new ArrayList<>();
        }
        
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();
        
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        visited[start] = true;
        
        ArrayList<Integer> path = new ArrayList<>();
        path.add(start);
        allSteps.add(new ArrayList<>(path));
        
        // Initial direction facing right on the maze
        Direction facing = Direction.RIGHT;
        
        while (!stack.isEmpty()) {
            int current = stack.peek();
            
            if (current == goal) {
                break;
            }
            
            
            Direction rightDirection = facing.turnRight();
            Direction forwardDirection = facing;
            Direction leftDirection = facing.turnLeft();
            Direction backDirection = facing.turnAround();
            
            // Essayer d'abord de tourner à droite
            int next = tryMove(current, visited, rightDirection);
            
            if (next != -1) {
                // Turn to the right
                facing = rightDirection;
            } else {
                // Go straight
                next = tryMove(current, visited, forwardDirection);
                
                if (next != -1) {
                    // Can go straight
                    facing = forwardDirection;
                } else {
                    // Else try to turn left
                    next = tryMove(current, visited, leftDirection);
                    
                    if (next != -1) {
                        // Go left
                        facing = leftDirection;
                    } else {
                        // Else try to go back
                        next = tryMove(current, visited, backDirection);
                        
                        if (next != -1) {
                            // Go back
                            facing = backDirection;
                        }
                    }
                }
            }
            
            if (next != -1) {
                //Found a valid move
                stack.push(next);
                visited[next] = true;
                path.add(next);
                allSteps.add(new ArrayList<>(path));
            } else {
                // ANo valid move, backtrack
                int removedVertex = stack.pop();
                
                // Check if we can go back to the previous vertex
                if (!stack.isEmpty() && !path.isEmpty()) {
                    int previous = stack.peek();
                    Direction backtrackDir = getMovementDirection(previous, removedVertex);
                    if (backtrackDir != null) {
                        facing = backtrackDir.turnAround();
                    }
                }
                
                if (!path.isEmpty()) {
                    path.remove(path.size() - 1);
                    // Add a step if the path change
                    if (!allSteps.isEmpty() && !path.equals(allSteps.get(allSteps.size() - 1))) {
                        allSteps.add(new ArrayList<>(path));
                    }
                }
            }
        }
        System.out.println("Path found: " + path);
        
        return allSteps;
    }

    /**
     */
    public void visualize() {
        // Take a look at the start and end points
        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Please set a start and end point.");
            return;
        }

        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> steps = solveRightSteps();
        
        if (steps.isEmpty()) {
            System.out.println("Impossible to solve the maze.");
            return;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Duration of Right walk solver : " + duration + " ms");
        mazeView.visualiseStep(steps);
    }
}