package org.mazeApp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
    *Gestionnary of the saved mazes
    * This class is responsible for saving and loading mazes from a file.
    * It allows the user to save mazes with a unique name and retrieve them later.
 */
public class SaveManager {
    
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt";
    
    /**
     * Internal class representing a saved maze. 
     */
    public static class SavedMaze {
        private final int seed;
        private final int rows;
        private final int columns;

        public SavedMaze(int seed, int rows, int columns) {
            this.seed = seed;
            this.rows = rows;
            this.columns = columns;
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
    }
    
    /**
     * Constructor for SaveManager.
     * Initialize the savedMazes HashMap and load mazes from the file.
     */
    public SaveManager() {
        this.savedMazes = new HashMap<>();
        loadMazesFromFile();
    }
    
    /**
     * Save a maze with a unique name.
     * 
     * @param rows  Rows number
     * @param columns Columns number
     * @param seed Seed value for the maze generation
     * @return The only name of the maze
     */
    public String saveMaze(int rows, int columns, int seed) {
        String mazeName = "Maze_" + System.currentTimeMillis(); // Génerate a unique name based on the current time
        
        // Verify if the maze already exists
        for (SavedMaze savedMaze : savedMazes.values()) {
            if (savedMaze.getSeed() == seed && savedMaze.getRows() == rows && savedMaze.getColumns() == columns) {
                System.out.println("Ce labyrinthe existe déjà dans la liste sauvegardée.");
                return null;
            }
        }
        
        // Save the maze
        savedMazes.put(mazeName, new SavedMaze(seed, rows, columns));
        saveMazesToFile(); // Keep the file updated
        System.out.println("Mazes saved with " + mazeName);
        
        return mazeName;
    }
    
    /**
     * Collect a saved maze by its name.
     * 
     * @param mazeName Maze name
     * @return LMaze save if it exists, null otherwise
     */
    public SavedMaze getSavedMaze(String mazeName) {
        return savedMazes.get(mazeName);
    }
    
    /**
     * Recup all saved mazes.
     * 
     * @return An Hashmap getting all saved mazes
     */
    public HashMap<String, SavedMaze> getAllSavedMazes() {
        return savedMazes;
    }
    
    /**
     * Verify if there are any saved mazes.
     * 
     * @return true if there are saved mazes, false otherwise
     */
    public boolean hasSavedMazes() {
        return !savedMazes.isEmpty();
    }
    
    /**
     * Saves all mazes to a file.
     */
    private void saveMazesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String mazeName : savedMazes.keySet()) {
                SavedMaze savedMaze = savedMazes.get(mazeName);
                writer.write(String.format("%s,%d,%d,%d\n", mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns()));
            }
            System.out.println("Mazes saved to file.");
        } catch (IOException e) {
            System.out.println("Error during the loaded mazes" + e.getMessage());
        }
    }
    
    /**
     * Charge tous les labyrinthes depuis le fichier.
     */
    private void loadMazesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String mazeName = parts[0];
                    int seed = Integer.parseInt(parts[1]);
                    int rows = Integer.parseInt(parts[2]);
                    int columns = Integer.parseInt(parts[3]);
                    savedMazes.put(mazeName, new SavedMaze(seed, rows, columns));
                }
            }
            System.out.println("Mazes loaded from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No file found. No mazes loaded.");
        } catch (IOException e) {
            System.out.println("Error no saved mazes" + e.getMessage());
        }
    }
    
    /**
     * Delete a saved maze by its name.
     * 
     * @param mazeName Name of the maze to delete
     * @return     true if the maze was deleted, false if it didn't exist
     */
    public boolean deleteSavedMaze(String mazeName) {
        SavedMaze removed = savedMazes.remove(mazeName);
        if (removed != null) {
            saveMazesToFile(); 
            System.out.println("Labyrinthe supprimé: " + mazeName);
            return true;
        }
        return false;
    }
}