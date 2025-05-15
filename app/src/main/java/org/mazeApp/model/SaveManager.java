package org.mazeApp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Gestionnaire de sauvegarde pour les labyrinthes.
 * Cette classe est responsable de la sauvegarde et du chargement des labyrinthes.
 */
public class SaveManager {
    
    private HashMap<String, SavedMaze> savedMazes;
    private static final String FILE_PATH = "savedMazes.txt";
    
    /**
     * Classe interne pour représenter un labyrinthe sauvegardé.
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
     * Constructeur du gestionnaire de sauvegarde.
     * Initialise le stockage et charge les labyrinthes depuis le fichier.
     */
    public SaveManager() {
        this.savedMazes = new HashMap<>();
        loadMazesFromFile();
    }
    
    /**
     * Sauvegarde un labyrinthe avec un nom unique.
     * 
     * @param rows Nombre de lignes
     * @param columns Nombre de colonnes
     * @param seed Valeur de seed pour générer le labyrinthe
     * @return Le nom unique attribué au labyrinthe sauvegardé ou null si le labyrinthe existe déjà
     */
    public String saveMaze(int rows, int columns, int seed) {
        String mazeName = "Maze_" + System.currentTimeMillis(); // Génère un nom unique
        
        // Vérifie les doublons
        for (SavedMaze savedMaze : savedMazes.values()) {
            if (savedMaze.getSeed() == seed && savedMaze.getRows() == rows && savedMaze.getColumns() == columns) {
                System.out.println("Ce labyrinthe existe déjà dans la liste sauvegardée.");
                return null;
            }
        }
        
        // Sauvegarde le labyrinthe en mémoire et dans le fichier
        savedMazes.put(mazeName, new SavedMaze(seed, rows, columns));
        saveMazesToFile(); // Persiste les changements
        System.out.println("Labyrinthe sauvegardé sous: " + mazeName);
        
        return mazeName;
    }
    
    /**
     * Récupère un labyrinthe sauvegardé par son nom.
     * 
     * @param mazeName Nom du labyrinthe
     * @return Le labyrinthe sauvegardé ou null s'il n'existe pas
     */
    public SavedMaze getSavedMaze(String mazeName) {
        return savedMazes.get(mazeName);
    }
    
    /**
     * Récupère tous les labyrinthes sauvegardés.
     * 
     * @return Une HashMap contenant tous les labyrinthes sauvegardés
     */
    public HashMap<String, SavedMaze> getAllSavedMazes() {
        return savedMazes;
    }
    
    /**
     * Vérifie si des labyrinthes ont été sauvegardés.
     * 
     * @return true si des labyrinthes sont sauvegardés, false sinon
     */
    public boolean hasSavedMazes() {
        return !savedMazes.isEmpty();
    }
    
    /**
     * Sauvegarde tous les labyrinthes dans un fichier pour un stockage permanent.
     */
    private void saveMazesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String mazeName : savedMazes.keySet()) {
                SavedMaze savedMaze = savedMazes.get(mazeName);
                writer.write(String.format("%s,%d,%d,%d\n", mazeName, savedMaze.getSeed(), savedMaze.getRows(), savedMaze.getColumns()));
            }
            System.out.println("Labyrinthes sauvegardés dans le fichier.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des labyrinthes: " + e.getMessage());
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
            System.out.println("Labyrinthes chargés depuis le fichier.");
        } catch (FileNotFoundException e) {
            System.out.println("Aucun fichier de labyrinthes sauvegardés trouvé. Démarrage à neuf.");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement des labyrinthes: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un labyrinthe sauvegardé.
     * 
     * @param mazeName Nom du labyrinthe à supprimer
     * @return true si le labyrinthe a été supprimé, false sinon
     */
    public boolean deleteSavedMaze(String mazeName) {
        SavedMaze removed = savedMazes.remove(mazeName);
        if (removed != null) {
            saveMazesToFile(); // Persiste les changements
            System.out.println("Labyrinthe supprimé: " + mazeName);
            return true;
        }
        return false;
    }
}