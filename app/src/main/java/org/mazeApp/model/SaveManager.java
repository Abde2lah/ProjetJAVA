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
 * Gestionnaire des labyrinthes sauvegardés
 * Cette classe est responsable de sauvegarder et charger les labyrinthes depuis un fichier.
 * Elle permet à l'utilisateur de sauvegarder des labyrinthes avec un nom unique et de les récupérer plus tard.
 */
public class SaveManager {
    
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt";
    
    /**
     * representation of a saved maze : 
     * exemple : Maze_1747594236929,5,4,4,5,6,5,9,6,10,9,10
     * 5=SEED
     * 4=ROWS
     * 4=COLUMNS
     * (5, 6) (5, 9) (6, 10) (9, 10) = Edges
     */
    public static class SavedMaze {
        private final int seed;
        private final int rows;
        private final int columns;
        private final ArrayList<Edges> edges; // Nouvelle propriété pour stocker les arêtes

        /**
         * Constructeur simple pour la rétrocompatibilité
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
     * @param rows Nombre de lignes
     * @param columns Nombre de colonnes
     * @param seed Valeur de la graine pour la génération du labyrinthe
     * @return Le nom unique du labyrinthe
     */
    public String saveMaze(int rows, int columns, int seed) {
        return saveMaze(rows, columns, seed, null);
    }
    /**
     * Sauvegarde un labyrinthe avec un nom unique et des arêtes personnalisées.
     * 
     * @param rows Nombre de lignes
     * @param columns Nombre de colonnes
     * @param seed Valeur de la graine pour la génération du labyrinthe
     * @param edges Liste des arêtes du labyrinthe (peut être null)
     * @return Le nom unique du labyrinthe
     */
    public String saveMaze(int rows, int columns, int seed, ArrayList<Edges> edges) {
        String mazeName = "Maze_" + System.currentTimeMillis(); // Génère un nom unique basé sur l'heure actuelle
        
        // Vérifie si le labyrinthe existe déjà (uniquement par seed, rows, columns - pas par structure)
        for (SavedMaze savedMaze : savedMazes.values()) {
            if (savedMaze.getSeed() == seed && savedMaze.getRows() == rows && savedMaze.getColumns() == columns) {
                if (edges != null) {
                    continue;
                }
                System.out.println("Ce labyrinthe existe déjà dans la liste sauvegardée.");
                return null;
            }
        }
        
        // Sauvegarde le labyrinthe avec ou sans arêtes personnalisées
        SavedMaze newMaze;
        if (edges != null) {
            newMaze = new SavedMaze(seed, rows, columns, edges);
        } else {
            newMaze = new SavedMaze(seed, rows, columns);
        }
        
        savedMazes.put(mazeName, newMaze);
        saveMazesToFile(); // Garde le fichier à jour
        System.out.println("Labyrinthe sauvegardé sous le nom " + mazeName);
        
        return mazeName;
    }
    
    /**
     * Sauvegarde un graphe complet avec sa structure.
     * 
     * @param graph Le graphe à sauvegarder
     * @return Le nom unique du labyrinthe
     */
    public String saveMaze(Graph graph) {
        if (graph == null) {
            System.out.println("Impossible de sauvegarder un graphe null.");
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
            System.out.println("Labyrinthe introuvable: " + mazeName);
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
            System.out.println("Labyrinthes sauvegardes dans le fichier.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des labyrinthes : " + e.getMessage());
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
            System.out.println("Labyrinthes chargés depuis le fichier.");
        } catch (FileNotFoundException e) {
            System.out.println("Aucun fichier trouvé. Pas de labyrinthes chargés.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erreur lors du chargement des labyrinthes : " + e.getMessage());
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
            System.out.println("Labyrinthe supprimé: " + mazeName);
            return true;
        }
        return false;
    }
}