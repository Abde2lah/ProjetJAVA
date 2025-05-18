package org.mazeApp.view;

import java.io.*;
import java.util.*;
import org.mazeApp.model.Graph;
import org.mazeApp.model.Edges;
import org.mazeApp.model.algorithms.*;

public class TerminalView {

    private static final Scanner scanner = new Scanner(System.in);
    private static final File SAVE_FILE = new File("savedMazes.txt");

    public static void main(String[] args) {
        System.out.println("==== Terminal Maze Solver ====");

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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE, true))) {
                writer.write(rows + "," + cols + "," + seed);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Failed to save maze info.");
            }

        } else if (mazeChoice.equals("2")) {
            List<String> savedMazes = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    savedMazes.add(line);
                }
            } catch (IOException e) {
                System.out.println("No saved mazes found.");
                return;
            }

            if (savedMazes.isEmpty()) {
                System.out.println("No saved mazes available.");
                return;
            }

            System.out.println("Select a maze:");
            for (int i = 0; i < savedMazes.size(); i++) {
                System.out.println((i + 1) + ": " + savedMazes.get(i));
            }
            System.out.print("Choice: ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= savedMazes.size()) {
                System.out.println("Invalid choice.");
                return;
            }
            String[] parts = savedMazes.get(index).split(",");
            rows = Integer.parseInt(parts[0]);
            cols = Integer.parseInt(parts[1]);
            seed = Integer.parseInt(parts[2]);
            graph = new Graph(seed, rows, cols);
            graph.getAllNeighbours();
        } else {
            System.out.println("Invalid input. Exiting...");
            return;
        }

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
                if (!steps.isEmpty()) {
                    path = steps.get(steps.size() - 1);
                } else {
                    path = new ArrayList<>();
                }
                break;
            }
            case "4": {
                DijkstraSolver dijkstra = new DijkstraSolver(graph, null, null);
                ArrayList<ArrayList<Integer>> steps = dijkstra.getDijkstraSteps(start, end);
                if (!steps.isEmpty()) {
                    path = steps.get(steps.size() - 1);
                } else {
                    path = new ArrayList<>();
                }
                break;
            }
            case "5": {
                RandomSolver random = new RandomSolver(graph, null);
                ArrayList<ArrayList<Integer>> steps = random.solveRandomWalkSteps();
                if (!steps.isEmpty()) {
                    path = steps.get(steps.size() - 1);
                } else {
                    path = new ArrayList<>();
                }
                break;
            }
            case "6": {
                OnlyLeftSolver left = new OnlyLeftSolver(graph, null);
                ArrayList<ArrayList<Integer>> steps = left.solveLeftSteps();
                if (!steps.isEmpty()) {
                    path = steps.get(steps.size() - 1);
                } else {
                    path = new ArrayList<>();
                }
                break;
            }
            case "7": {
                OnlyRightSolver right = new OnlyRightSolver(graph, null);
                ArrayList<ArrayList<Integer>> steps = right.solveRightSteps();
                if (!steps.isEmpty()) {
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
            System.out.println("\nPath found:");
            System.out.println(path);
            System.out.println("\nLength: " + path.size());
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
        } else {
            System.out.println("\nNo path found.");
        }
    }
}
