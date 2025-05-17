package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

public class OnlyLeftSolver {

    private ArrayList<ArrayList<Edges>> graphMaze;
    private Graph graph;
    private MazeView mazeView;
    private int start;
    private int goal;
    private int vertexCount;
    private int columns;
    private int rows;

    /**
    Create a solver for the maze using the left-hand rule
     * 
     * @param graph The graph representing the maze
     * @param mazeView The view for visualizing the maze
     */
    public OnlyLeftSolver(Graph graph, MazeView mazeView) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.columns = graph.getColumns();
        this.rows = graph.getRows();
        this.mazeView = mazeView;
        
    }

    /**
     * Cardinal directions for the left-hand rule
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
         * Calcule the direction after a right turn
         */
        public Direction turnRight() {
            return values()[(value + 1) % 4];
        }
        
        /**
         * Calcule the direction after a left turn
         */
        public Direction turnLeft() {
            return values()[(value + 3) % 4]; // équivalent à (value - 1 + 4) % 4
        }
        
        /**
         * Calcule the direction after a 180° turn
         */
        public Direction turnAround() {
            return values()[(value + 2) % 4];
        }
    }

    /**
     * Determinate if the movement between two vertices corresponds to a specific direction
     * 
     * @param current The current vertex
     * @param neighbor The potential neighboring vertex
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
     * DDtermine the direction of movement between two vertices
     * 
     * @param from Starting vertex
     * @param to Ending vertex
     * @return Direction of movement (UP, DOWN, LEFT, RIGHT) or null if not adjacent
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
     * Try to move in a specific direction from the current vertex
     * 
     * @param current Current vertex
     * @param visited Table of visited vertices
     * @param direction Favorite direction
     * @return The next vertex in the specified direction, or -1 if not possible
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
     * Solve the maze using the left-hand rule
     * 
     * @return Steps taken to solve the maze
     */
    public ArrayList<ArrayList<Integer>> solveLeftSteps() {
        // Veirification of initialization
        this.start = mazeView.getStartIndex();
        this.goal = mazeView.getEndIndex();
        if (start < 0 || goal < 0 || start >= vertexCount || goal >= vertexCount) {
            System.out.println("Points de départ ou d'arrivée invalides.");
            return new ArrayList<>();
        }
        
        boolean[] visited = new boolean[vertexCount];

        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        long endTime = System.currentTimeMillis(); 
        long duration = endTime - startTime; 
        System.out.println("Durée de l'algorithme OnlyLeftSolver : " + duration + " ms");
        
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
            
            Direction leftDirection = facing.turnLeft();
            Direction forwardDirection = facing;
            Direction rightDirection = facing.turnRight();
            Direction backDirection = facing.turnAround();
            
            // Try to move in the left direction first
            int next = tryMove(current, visited, leftDirection);
            
            if (next != -1) {
                // Turn to the left
                facing = leftDirection;
            } else {
                // try to go straight
                next = tryMove(current, visited, forwardDirection);
                
                if (next != -1) {
                    // We can go straight
                    facing = forwardDirection;
                } else {
                    // Try to turn right
                    next = tryMove(current, visited, rightDirection);
                    
                    if (next != -1) {
                        // We can go right
                        facing = rightDirection;
                    } else {
                        // Try to go back
                        next = tryMove(current, visited, backDirection);
                        
                        if (next != -1) {
                            // We can go back
                            facing = backDirection;
                        }
                    }
                }
            }
            
            if (next != -1) {
                // Path found, move to the next vertex
                stack.push(next);
                visited[next] = true;
                path.add(next);
                allSteps.add(new ArrayList<>(path));
            } else {
                // No valid move, backtrack
                int removedVertex = stack.pop();
                
                // Determine the direction to backtrack
                if (!stack.isEmpty() && !path.isEmpty()) {
                    int previous = stack.peek();
                    Direction backtrackDir = getMovementDirection(previous, removedVertex);
                    if (backtrackDir != null) {
                        facing = backtrackDir.turnAround();
                    }
                }
                
                if (!path.isEmpty()) {
                    path.remove(path.size() - 1);
                    // Add the current path to allSteps if it's not already there
                    if (!allSteps.isEmpty() && !path.equals(allSteps.get(allSteps.size() - 1))) {
                        allSteps.add(new ArrayList<>(path));
                    }
                }
            }
        }
        System.out.println("Path found : " + path);
        return allSteps;
    }

    /**
     * Visualize the maze solving process
     */
    public void visualize() {
        // Veirification of initialization
        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> steps = solveLeftSteps();
        
        if (steps.isEmpty()) {
            System.out.println("Impossible de résoudre le labyrinthe.");
            return;
        }
        long endTime = System.currentTimeMillis();   
        long duration = endTime - startTime;
        System.out.println("Durée de l'algorithme OnlyLeftSolver : " + duration + " ms");
        
        mazeView.visualiseStep(steps);
    }
}