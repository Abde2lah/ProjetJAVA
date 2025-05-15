package org.mazeApp.model.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.mazeApp.model.Edges;

public class DFSGenerator implements MazeGenerator {
    @Override
    public ArrayList<Edges> generate(int rows, int columns, int seed) {
        ArrayList<Edges> generationSteps = new ArrayList<>();
        boolean[] visited = new boolean[rows * columns];
        Random random = new Random(seed);
        
        // Commence à un point aléatoire
        int start = random.nextInt(rows * columns);
        dfsGenerate(start, rows, columns, visited, generationSteps, random);
        
        return generationSteps;
    }
    
    @Override
    public String getName() {
        return "DFS";
    }
    
    private void dfsGenerate(int current, int rows, int columns, boolean[] visited, 
                             ArrayList<Edges> steps, Random random) {
        visited[current] = true;
        
        // Obtenir tous les voisins potentiels
        ArrayList<Integer> neighbors = new ArrayList<>();
        
        // Voisin à droite
        if ((current % columns) < columns - 1) {
            neighbors.add(current + 1);
        }
        // Voisin à gauche
        if ((current % columns) > 0) {
            neighbors.add(current - 1);
        }
        // Voisin en bas
        if (current + columns < rows * columns) {
            neighbors.add(current + columns);
        }
        // Voisin en haut
        if (current - columns >= 0) {
            neighbors.add(current - columns);
        }
        
        // Mélanger les voisins pour rendre la génération pseudo-aléatoire
        Collections.shuffle(neighbors, random);
        
        // Visiter chaque voisin non visité
        for (int neighbor : neighbors) {
            if (!visited[neighbor]) {
                steps.add(new Edges(current, neighbor));
                dfsGenerate(neighbor, rows, columns, visited, steps, random);
            }
        }
    }
}