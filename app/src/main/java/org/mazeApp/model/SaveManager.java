package org.mazeApp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manager class responsible for saving and loading mazes to/from a file.
 * <p>
 * This class allows users to persist maze configurations (including edges),
 * associate them with unique names, and retrieve or delete them later.
 * </p>
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class SaveManager {
    
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt";
    
/**
 * Represents a saved maze with seed, dimensions, and edge list.
 * <p>
 * Format example:
 * Maze_1747594236929,5,4,4,5,6,5,9,6,10,9,10
 * where:
 * 5 = seed, 4 = rows, 4 = columns, (5,6),(5,9),(6,10),(9,10) = edges
 * </p>
 */
    public static class SavedMaze {
        private final int seed;
        private final int rows;
        private final int columns;
        private final ArrayList<Edges> edges; // Nouvelle propriété pour stocker les arêtes

        /**
         * Initializes the SaveManager by loading all saved mazes from file.
         */
        public SavedMaze(int seed, int rows, int columns) {
            this.seed = seed;
            this.rows = rows;
            this.columns = columns;
            this.edges = new ArrayList<>();
        }

        /**
         * Constructor for a saved graph with custom edges
         * @param seed
         * @param rows
         * @param columns
         * @param edges
         */
        public SavedMaze(int seed, int rows, int columns, ArrayList<Edges> edges) {
            this.seed = seed;
            this.rows = rows;
            this.columns = columns;
            this.edges = edges;
        }

        public int getSeed() {
            return seed;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }
        
        public ArrayList<Edges> getEdges() {
            return edges;
        }
        
        public void addEdge(Edges edge) {
            this.edges.add(edge);
        }
    }
    
    /**
     * Constructor : this method initializes the savedMazes HashMap and loads mazes from the file.
     */
    public SaveManager() {
        this.savedMazes = new HashMap<>();
        loadMazesFromFile();
    }
    
    /**
     * Save a maze maze a unique name.
     * 
     * @param rows Number of rows
     * @param columns Number of columns
     * @param seed Value of the seed for the generation
     * @return Unique name for the maze
     */
    public String saveMaze(int rows, int columns, int seed) {
        return saveMaze(rows, columns, seed, null);
    }

    /**
     * Saves a maze with a given list of custom edges.
     *
     * @param rows number of rows
     * @param columns number of columns
     * @param seed generation seed
     * @param edges list of edges (optional, can be null)
     * @return the generated maze name, or null if duplicate
     */
    public String saveMaze(int rows, int columns, int seed, ArrayList<Edges> edges) {
        String mazeName = "Maze_" + System.currentTimeMillis(); // Génère un nom unique basé sur l'heure actuelle
        
        // Vérifie si le labyrinthe existe déjà (uniquement par seed, rows, columns - pas par structure)
        for (SavedMaze savedMaze : savedMazes.values()) {
            if (savedMaze.getSeed() == seed && savedMaze.getRows() == rows && savedMaze.getColumns() == columns) {
                if (edges != null) {
                    continue;
                }
                System.out.println("This maze already exists in the file");
                return null;
            }
        }
        
        // Save the maze with or without the customised edges
        SavedMaze newMaze;
        if (edges != null) {
            newMaze = new SavedMaze(seed, rows, columns, edges);
        } else {
            newMaze = new SavedMaze(seed, rows, columns);
        }
        
        savedMazes.put(mazeName, newMaze);
        saveMazesToFile(); // Garde le fichier à jour
        System.out.println("Maze saved with the name : " + mazeName);
        
        return mazeName;
    }
    
    /**
     * Saves an entire Graph instance, extracting its edge list.
     *
     * @param graph the graph to save
     * @return the unique name of the saved maze
     */
    public String saveMaze(Graph graph) {
        if (graph == null) {
            System.out.println("Impossible to save a null graph");
            return null;
        }
        
        int rows = graph.getRows();
        int columns = graph.getColumns();
        int seed = graph.getSeed();
        
        ArrayList<Edges> allEdges = new ArrayList<>();
        ArrayList<ArrayList<Edges>> graphMaze = graph.getGraphMaze();
        
        // We don't want to add the same edge twice, so we only add edges where source < destination
        for (int i = 0; i < graphMaze.size(); i++) {
            for (Edges edge : graphMaze.get(i)) {
                if (edge.getSource() < edge.getDestination()) {
                    allEdges.add(edge);
                }
            }
        }
        return saveMaze(rows, columns, seed, allEdges);
    }

    /**
     * Get a saved maze by its name.
     * 
     * @param mazeName Name of the maze
     * @return The saved maze object, or null if not found
     */
    public SavedMaze getSavedMaze(String mazeName) {
        return savedMazes.get(mazeName);
    }
    
    /**
     * Get all saved mazes.
     * 
     * @return A HashMap of saved mazes with their names as keys
     */
    public HashMap<String, SavedMaze> getAllSavedMazes() {
        return savedMazes;
    }
    
    /**
     * Check if there are any saved mazes.
     * 
     * @return true if there are saved mazes, false otherwise
     */
    public boolean hasSavedMazes() {
        return !savedMazes.isEmpty();
    }
    
    /**
     * Build a graph from a saved maze.
     * 
     * @param mazeName Name of the saved maze
     * @return The graph built from the saved maze, or null if not found
     * 
     */
    public Graph buildGraph(String mazeName) {
        SavedMaze savedMaze = savedMazes.get(mazeName);
        if (savedMaze == null) {
            System.out.println("Mazes unfoundable: " + mazeName);
            return null;
        }
        // Otherwise, we need to create a new graph and add the edges manually
        Graph graph = Graph.emptyGraph(savedMaze.getRows(), savedMaze.getColumns());
        graph.setSeed(savedMaze.getSeed());
        
        for (Edges edge : savedMaze.getEdges()) {
            graph.addEdge(edge.getSource(), edge.getDestination());
        }
        return graph;
    }
    
    /**
     * We save the mazes to a file in a specific format.
     * Format: mazeName,seed,rows,columns,edges...
     */
    private void saveMazesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String mazeName : savedMazes.keySet()) {
                SavedMaze savedMaze = savedMazes.get(mazeName);
                
                // Format de base: nom,seed,rows,columns
                StringBuilder line = new StringBuilder(String.format("%s,%d,%d,%d", 
                mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns()));
                for (Edges edge : savedMaze.getEdges()) {
                    line.append(String.format(",%d,%d", edge.getSource(), edge.getDestination()));
                }
                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("Mazes saved on the files");
        } catch (IOException e) {
            System.out.println("Error during the saving of the mazes : " + e.getMessage());
        }
    }
    
    /**
     * This method loads mazes from a file.
     */
    private void loadMazesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                // We check if the line has at least 4 parts (name, seed, rows, columns)
                if (parts.length >= 4) {
                    String mazeName = parts[0];
                    int seed = Integer.parseInt(parts[1]);
                    int rows = Integer.parseInt(parts[2]);
                    int columns = Integer.parseInt(parts[3]);                    
                    SavedMaze maze = new SavedMaze(seed, rows, columns);
                    // If there are edges, we add them
                    if (parts.length > 4) {
                        for (int i = 4; i < parts.length; i += 2) {
                            if (i + 1 < parts.length) {
                                int source = Integer.parseInt(parts[i]);
                                int destination = Integer.parseInt(parts[i + 1]);
                                maze.addEdge(new Edges(source, destination));
                            }
                        }
                    }
                    savedMazes.put(mazeName, maze);
                }
            }
            System.out.println("Mazes loaded from the file");
        } catch (FileNotFoundException e) {
            System.out.println("No file found, no maze loaded.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error during the loading of the mazes : " + e.getMessage());
        }
    }
    
    /**
     * Delete a saved maze by its name.
     * 
     * @param mazeName Name of the maze to delete
     * @return true if the maze was deleted, false if it was not found
     */
    public boolean deleteSavedMaze(String mazeName) {
        SavedMaze removed = savedMazes.remove(mazeName);
        if (removed != null) {
            saveMazesToFile(); 
            System.out.println("Maze deleted: " + mazeName);
            return true;
        }
        return false;
    }
}