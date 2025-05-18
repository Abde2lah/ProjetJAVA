package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

public class OnlyRightSolver {

    private ArrayList<ArrayList<Edges>> graphMaze;
    private Graph graph;
    private MazeView mazeView; // peut être null pour le terminal
    private int start;
    private int goal;
    private int vertexCount;
    private int columns;
    private int rows;

    // UI constructor
    public OnlyRightSolver(Graph graph, MazeView mazeView) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.columns = graph.getColumns();
        this.rows = graph.getRows();
        this.mazeView = mazeView;
        this.start = mazeView != null ? mazeView.getStartIndex() : -1;
        this.goal = mazeView != null ? mazeView.getEndIndex() : -1;
    }

    // Terminal constructor
    public OnlyRightSolver(Graph graph, int start, int end) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.columns = graph.getColumns();
        this.rows = graph.getRows();
        this.mazeView = null;
        this.start = start;
        this.goal = end;
    }

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

    private int tryMove(int current, boolean[] visited, Direction direction) {
        if (current < 0 || current >= graphMaze.size()) return -1;
        for (Edges edge : graphMaze.get(current)) {
            int neighbor = edge.getDestination();
            if (!visited[neighbor] && isDirection(current, neighbor, direction)) return neighbor;
        }
        return -1;
    }

    public ArrayList<ArrayList<Integer>> solveRightSteps() {
        if (mazeView != null) {
            this.start = mazeView.getStartIndex();
            this.goal = mazeView.getEndIndex();
        }

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

        Direction facing = Direction.RIGHT;

        while (!stack.isEmpty()) {
            int current = stack.peek();
            if (current == goal) break;

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

        System.out.println("Path found: " + path);
        return allSteps;
    }

    public void visualize() {
        if (mazeView == null) {
            System.out.println("Visualisation non disponible en mode terminal.");
            return;
        }

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