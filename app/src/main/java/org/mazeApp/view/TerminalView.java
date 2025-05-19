package org.mazeApp.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.algorithms.AStarSolver;
import org.mazeApp.model.algorithms.BFSsolver;
import org.mazeApp.model.algorithms.DFSsolver;
import org.mazeApp.model.algorithms.DijkstraSolver;
import org.mazeApp.model.algorithms.OnlyLeftSolver;
import org.mazeApp.model.algorithms.OnlyRightSolver;
import org.mazeApp.model.algorithms.RandomSolver;

public class TerminalView {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        System.out.println(" Terminal Maze Solver ");

        Graph graph = null;
        int rows = 0, cols = 0, seed = 0;

        System.out.println("1 - Create new maze");
        System.out.println("2 - Load saved maze");
        System.out.print("Your choice: ");
        String mazeChoice = scanner.nextLine();

        if (mazeChoice.equals("1")) {
            System.out.print("Enter number of rows: ");
            rows = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter number of columns: ");
            cols = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter seed: ");
            seed = Integer.parseInt(scanner.nextLine());
            graph = new Graph(seed, rows, cols);
            graph.getAllNeighbours();

            System.out.print("Enter start index: ");
            int start = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter end index: ");
            int end = Integer.parseInt(scanner.nextLine());

            System.out.println("Choose algorithm:");
            System.out.println("1 - BFS");
            System.out.println("2 - DFS");
            System.out.println("3 - A*");
            System.out.println("4 - Dijkstra");
            System.out.println("5 - Random Walk");
            System.out.println("6 - Only Left");
            System.out.println("7 - Only Right");

            String choice = scanner.nextLine();
            List<Integer> path = null;

            long startTime = System.currentTimeMillis();

            switch (choice) {
                case "1": {
                    BFSsolver bfs = new BFSsolver(graph.getVertexNb());
                    //path = bfs.visualize(start, end, graph.getGraphMaze());
                    break;
                }
                case "2": {
                    DFSsolver dfs = new DFSsolver(graph, null, null);
                    path = dfs.findPath(start, end);
                    break;
                }
                case "3": {
                    AStarSolver astar = new AStarSolver(graph, null, null);
                    ArrayList<ArrayList<Integer>> steps = astar.getAStarSteps(start, end);
                    if (steps != null && !steps.isEmpty()) {
                        path = steps.get(steps.size() - 1);
                    } else {
                        path = new ArrayList<>();
                    }
                    break;
                }
                case "4": {
                    DijkstraSolver dijkstra = new DijkstraSolver(graph, null, null);
                    ArrayList<ArrayList<Integer>> steps = dijkstra.getDijkstraSteps(start, end);
                    if (steps != null && !steps.isEmpty()) {
                        path = steps.get(steps.size() - 1);
                    } else {
                        path = new ArrayList<>();
                    }
                    break;
                }
                case "5": {
                    RandomSolver random = new RandomSolver(graph, start, end);
                    ArrayList<ArrayList<Integer>> steps = random.solveRandomWalkSteps();
                    if (steps != null && !steps.isEmpty()) {
                        path = steps.get(steps.size() - 1);
                    } else {
                        path = new ArrayList<>();
                    }
                    break;
                }
                case "6": {
                    OnlyLeftSolver left = new OnlyLeftSolver(graph, start, end);
                    ArrayList<ArrayList<Integer>> steps = left.solveLeftSteps();
                    if (steps != null && !steps.isEmpty()) {
                        path = steps.get(steps.size() - 1);
                    } else {
                        path = new ArrayList<>();
                    }
                    break;
                }
                case "7": {
                    OnlyRightSolver right = new OnlyRightSolver(graph, start, end);
                    ArrayList<ArrayList<Integer>> steps = right.solveRightSteps();
                    if (steps != null && !steps.isEmpty()) {
                        path = steps.get(steps.size() - 1);
                    } else {
                        path = new ArrayList<>();
                    }
                    break;
                }
                default:
                    System.out.println("Invalid choice.");
            }


            long endTime = System.currentTimeMillis();

            if (path != null && !path.isEmpty()) {
                System.out.println("\nPath found:\n" + path);
                System.out.println("\nLength: " + path.size());
                System.out.println("Execution time: " + (endTime - startTime) + " ms");
                printAsciiMaze(graph, rows, cols, path, start, end);
            } else {
                System.out.println("\nNo path found.");
            }
        }
    }

    public static void printAsciiMaze(Graph graph, int rows, int cols, List<Integer> path, int start, int end) {
        Set<Integer> pathSet = new HashSet<>(path);
        var maze = graph.getGraphMaze();

        for (int r = 0; r < rows; r++) {
            // top border
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                System.out.print("+");
                boolean topOpen = false;
                for (Edges e : maze.get(idx)) {
                    if (e.getDestination() == idx - cols) topOpen = true;
                }
                System.out.print(topOpen ? "    " : "----");
            }
            System.out.println("+");

            // content line
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                boolean leftOpen = false;
                for (Edges e : maze.get(idx)) {
                    if (e.getDestination() == idx - 1) leftOpen = true;
                }
                System.out.print(leftOpen ? " " : "|");
                String cell;
                if (idx == start) cell = GREEN + " S " + RESET;
                else if (idx == end) cell = RED + " E " + RESET;
                else if (pathSet.contains(idx)) cell = YELLOW + " **" + RESET;
                else cell = String.format("%2d ", idx);
                System.out.print(cell);
            }
            System.out.println("|");
        }

        // bottom border
        for (int c = 0; c < cols; c++) {
            System.out.print("+----");
        }
        System.out.println("+");
    }
}