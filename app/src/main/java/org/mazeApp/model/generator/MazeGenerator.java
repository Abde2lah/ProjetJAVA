package org.mazeApp.model.generator;

import java.util.ArrayList;

import org.mazeApp.model.Edges;


/**
 * Interface pour les différents algorithmes de génération de labyrinthe
 C'est tras simple : 
    * - On génère un labyrinthe
    * - On retourne les étapes de génération
    * - On retourne le nom de l'algorithme
    *
 
 */
public interface MazeGenerator {
    /**
     * Génère un labyrinthe et retourne les étapes de génération
     */
    ArrayList<Edges> generate(int rows, int columns, int seed);
    
    /**
     * Retourne le nom de l'algorithme
     */
    String getName();
}