package org.mazeApp.model;

import java.util.List;
import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Interface commune pour tous les algorithmes de résolution de labyrinthe
 */
public interface MazeSolver {
    /**
     * Exécute l'algorithme et renvoie le chemin final
     * @param start Point de départ
     * @param end Point d'arrivée
     * @return Le chemin trouvé, ou liste vide si aucun chemin
     */
    List<Integer> findPath(int start, int end);
    
    /**
     * Exécute l'algorithme avec visualisation en temps réel
     */
    void visualize();
    
    /**
     * Récupère le temps d'exécution de l'algorithme
     * @return Temps d'exécution en millisecondes
     */
    long getExecutionTime();
    
    /**
     * Récupère le chemin final trouvé
     * @return Liste des sommets constituant le chemin
     */
    List<Integer> getFinalPath();
    
    /**
     * Configure le solveur avec les éléments nécessaires
     * @param graph Le graphe représentant le labyrinthe
     * @param graphView La vue graphe pour visualisation
     * @param mazeView La vue labyrinthe pour visualisation
     * @return Le solveur lui-même (pour chaînage)
     */
    MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView);
}