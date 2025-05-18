package org.mazeApp.view;

import java.io.*;
import java.util.*;
import org.mazeApp.model.Graph;
import org.mazeApp.model.Edges;
import org.mazeApp.model.algorithms.*;

public class TerminalView {

    private static final Scanner scanner = new Scanner(System.in);

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
                path = bfs.visualize(start, end, graph.getGraphMaze());
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
                path = !steps.isEmpty() ? steps.get(steps.size() - 1) : new ArrayList<>();
                break;
            }
            case "4": {
                DijkstraSolver dijkstra = new DijkstraSolver(graph, null, null);
                ArrayList<ArrayList<Integer>> steps = dijkstra.getDijkstraSteps(start, end);
                path = !steps.isEmpty() ? steps.get(steps.size() - 1) : new ArrayList<>();
                break;
            }
            case "5": {
                RandomSolver random = new RandomSolver(graph, start, end);
                ArrayList<ArrayList<Integer>> steps = random.solveRandomWalkSteps();
                path = !steps.isEmpty() ? steps.get(steps.size() - 1) : new ArrayList<>();
                break;
            }
            case "6": {
                OnlyLeftSolver left = new OnlyLeftSolver(graph, start, end);
                ArrayList<ArrayList<Integer>> steps = left.solveLeftSteps();
                path = !steps.isEmpty() ? steps.get(steps.size() - 1) : new ArrayList<>();
                break;
            }
            case "7": {
                OnlyRightSolver right = new OnlyRightSolver(graph, start, end);
                ArrayList<ArrayList<Integer>> steps = right.solveRightSteps();
                path = !steps.isEmpty() ? steps.get(steps.size() - 1) : new ArrayList<>();
                break;
            }
            default:
                System.out.println("Invalid choice.");
        }

        long endTime = System.currentTimeMillis();

        if (path != null && !path.isEmpty()) {
            System.out.println("\nPath found:");
            System.out.println(path);
            System.out.println("\nLength: " + path.size());
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
        } else {
            System.out.println("\nNo path found.");
        }
    }
}
}
